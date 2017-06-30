package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.block.Machines
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.item.ItemBlockBase
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.modules.conveyor_belt.*
import com.cout970.magneticraft.util.getList
import com.cout970.magneticraft.util.list
import com.cout970.magneticraft.util.newNbt
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/06/17.
 */
class ModuleConveyorBelt(
        val facingGetter: () -> EnumFacing,
        override val name: String = "module_conveyor_belt"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()

    val boxes = mutableListOf<Box>()
    var toUpdate = false
    val newBoxes = mutableListOf<Box>()
    val bitmap = BitMap()
    var flip = true

    override fun update() {
        val map = getSurroundMap()
        if (world.shouldTick(pos, 20)) {
            resetBitmap()
        }
        advanceSimulation(map)
    }

    fun resetBitmap() {
        newBoxes.clear()
        newBoxes.addAll(boxes)
        toUpdate = true
    }

    fun advanceSimulation(surroundingMap: Map<BitmapLocation, IBitMap>) {

        val frontTile = world.getTile<TileConveyorBelt>(pos.offset(facing))

        //if there is no block in front don't go all the way
        val limit = if (frontTile == null) 13f else 15f
        val bitmap = FullBitMap(bitmap, surroundingMap)

        if (toUpdate) {
            boxes.forEach {
                bitmap.unmark(it.getHitBox())
            }
            boxes.clear()
            boxes.addAll(newBoxes)
            newBoxes.clear()
            boxes.forEach {
                bitmap.mark(it.getHitBox())
            }
            toUpdate = false
        }

        boxes.sortedBy { -it.position }.forEach {
            if (it.lastTick != world.totalWorldTime) {
                if (it.position <= limit) {

                    val speed = (if (it.route.isShort) 2f else 1f)

                    it.locked = checkCollision(bitmap, it, speed)
                    if (!it.locked) {
                        var hitbox = it.getHitBox()
                        bitmap.unmark(hitbox)

                        it.move(speed)
                        hitbox = it.getHitBox()
                        bitmap.mark(hitbox)
                    }
                } else {
                    it.locked = true
                }
                it.lastTick = world.totalWorldTime
            }
        }
        if (frontTile != null) {
            val removed = boxes.removeAll {
                if (it.position <= limit) return@removeAll false

                val bitMap = FullBitMap(bitmap, surroundingMap)
                bitMap.unmark(it.getHitBox())

                val res = frontTile.conveyorModule.addItem(it.item, facing,
                        it.route)
                if (!res) {
                    bitMap.mark(it.getHitBox())
                }
                res
            }
            if (removed) {
                container.sendUpdateToNearPlayers()
            }
        }
    }

    // Note: this cannot be called on deserializeNBT or the game will crash of StackOverflow
    fun getSurroundMap(): Map<BitmapLocation, IBitMap> {
        val map = mutableMapOf<BitmapLocation, IBitMap>()

        world.getTile<TileConveyorBelt>(pos.offset(facing.opposite))?.let {
            map += BitmapLocation.IN_BACK to it.conveyorModule.bitmap
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing.rotateY()))?.let {
            map += BitmapLocation.IN_RIGHT to it.conveyorModule.bitmap
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing.rotateYCCW()))?.let {
            map += BitmapLocation.IN_LEFT to it.conveyorModule.bitmap
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing))?.let {
            when (it.facing) {
                facing -> map += BitmapLocation.OUT_FRONT to it.conveyorModule.bitmap
                facing.opposite -> map += BitmapLocation.OUT_BACK to it.conveyorModule.bitmap
                facing.rotateY() -> map += BitmapLocation.OUT_RIGHT to it.conveyorModule.bitmap
                facing.rotateYCCW() -> map += BitmapLocation.OUT_LEFT to it.conveyorModule.bitmap
                else -> Unit
            }
        }
        return map
    }

    private fun checkCollision(thisBitMap: IBitMap, box: Box, speed: Float): Boolean {
        val thisTemp = thisBitMap.copy()
        val hitbox = box.getHitBox()

        thisTemp.unmark(hitbox)

        box.move(speed)
        val newHitbox = box.getHitBox()

        val tempResult = !thisTemp.test(newHitbox)
        box.move(-speed)
        return tempResult
    }

    fun addItem(stack: ItemStack): Boolean {
        val route = if (flip) {
            Route.LEFT_FORWARD
        } else {
            Route.RIGHT_FORWARD
        }
        flip = !flip
        val box = Box(stack.copy(), 2f, route, world.totalWorldTime)
        if (bitmap.test(box.getHitBox())) {
            boxes += box
            container.sendUpdateToNearPlayers()
            return true
        } else {
            return false
        }
    }

    fun addItem(stack: ItemStack, side: EnumFacing, oldRoute: Route): Boolean {
        val newRoute = getRoute(facing, side, oldRoute)
        val box = Box(stack.copy(), 0f, newRoute, world.totalWorldTime)
        if (bitmap.test(box.getHitBox())) {
            FullBitMap(bitmap, getSurroundMap()).mark(box.getHitBox())
            boxes += box
            container.sendUpdateToNearPlayers()
            return true
        } else {
            return false
        }
    }

    fun getRoute(facing: EnumFacing, side: EnumFacing, oldRoute: Route): Route {

        if (facing == side) {
            return if (!oldRoute.leftSide) Route.RIGHT_FORWARD else Route.LEFT_FORWARD
        }
        if (facing.opposite == side) {
            return if (oldRoute.leftSide) Route.RIGHT_FORWARD else Route.LEFT_FORWARD
        }
        if (facing.rotateY() == side) {
            return if (oldRoute.leftSide) Route.LEFT_SHORT else Route.LEFT_LONG
        }
        if (facing.rotateYCCW() == side) {
            return if (oldRoute.leftSide) Route.RIGHT_LONG else Route.RIGHT_SHORT
        }
        throw IllegalStateException("Illegal side: $side for facing: $facing")
    }


    fun removeItem(): ItemStack {
        if (boxes.isNotEmpty()) {
            val box = boxes.removeAt(boxes.lastIndex)
            val fullBitmap = FullBitMap(bitmap, getSurroundMap())
            fullBitmap.unmark(box.getHitBox())
            container.sendUpdateToNearPlayers()
            return box.item
        }
        return ItemStack.EMPTY
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        newBoxes.clear()
        newBoxes += nbt.getList("items")
                .map { it as NBTTagCompound }
                .map { Box(it) }
        toUpdate = true
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            list("items") {
                boxes.map { it.serializeNBT() }.forEach { appendTag(it) }
            }
        }
    }

    override fun onBreak() {
        val bitmap = FullBitMap(bitmap, getSurroundMap())
        boxes.forEach {
            bitmap.unmark(it.getHitBox())
            world.dropItem(it.item, pos)
        }
        boxes.clear()
    }

    override fun onActivated(it: OnActivatedArgs): Boolean {
        if (it.heldItem.isNotEmpty) {
            val item = it.heldItem.item
            if (item is ItemBlockBase && item.blockBase == Machines.conveyorBelt) {
                return false
            }
            val success = addItem(it.heldItem)
            if (success && world.isServer) {
                it.playerIn.setHeldItem(it.hand, ItemStack.EMPTY)
            }
            return true
        } else {
            val item = removeItem()
            if (item.isNotEmpty) {
                if (world.isServer) {
                    it.playerIn.inventory.addItemStackToInventory(item)
                }
                return true
            }
        }
        return false
    }
}