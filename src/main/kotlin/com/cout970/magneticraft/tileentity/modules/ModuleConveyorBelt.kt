package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.block.AutomaticMachines
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.item.itemblock.ItemBlockBase
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.getTile
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

    val boxes = mutableListOf<BoxedItem>()
    var toUpdate = false
    val newBoxes = mutableListOf<BoxedItem>()
    var flip = true

    fun generateGlobalBitMap(): IBitMap {
        val map = getSurroundMap()
        val bitmap = FullBitMap(BitMap(), map.entries.map { (key, _) -> key to BitMap() }.toMap())
        boxes.forEach { bitmap.mark(it.getHitBox()) }
        map.forEach { pos, mod ->
            mod.boxes.forEach {
                bitmap.mark(pos.fromExternalToLocal(it.getHitBox()))
            }
        }
        return bitmap
    }

    override fun update() {
        advanceSimulation()
    }

    fun advanceSimulation() {
        if (toUpdate) {
            boxes.clear()
            boxes.addAll(newBoxes)
            newBoxes.clear()
            toUpdate = false
        }

        val frontTile = world.getTile<TileConveyorBelt>(pos.offset(facing))

        //if there is no block in front don't go all the way
        val limit = if (frontTile == null) 13f else 15f

        val fullBitMap = generateGlobalBitMap()

        boxes.forEach {
            if (it.lastTick != world.totalWorldTime) {
                if (it.position <= limit) {

                    val speed = (if (it.route.isShort) 2f else 1f)

                    it.locked = checkCollision(fullBitMap, it, speed)
                    if (!it.locked) {
                        var hitbox = it.getHitBox()
                        fullBitMap.unmark(hitbox)

                        it.move(speed)
                        hitbox = it.getHitBox()
                        fullBitMap.mark(hitbox)
                    }
                } else {
                    it.locked = true
                }
                it.lastTick = world.totalWorldTime
            }
        }
        // Move to next belt
        if (frontTile != null) {
            val removed = boxes.removeAll {
                if (it.position <= limit) return@removeAll false
                frontTile.conveyorModule.addItem(it.item, facing, it.route)
            }
            if (removed) {
                container.sendUpdateToNearPlayers()
            }
        }
    }

    // Note: this cannot be called on deserializeNBT or the game will crash of StackOverflow
    fun getSurroundMap(): Map<BitmapLocation, ModuleConveyorBelt> {
        val map = mutableMapOf<BitmapLocation, ModuleConveyorBelt>()

        world.getTile<TileConveyorBelt>(pos.offset(facing.opposite))?.let {
            map += BitmapLocation.IN_BACK to it.conveyorModule
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing.rotateY()))?.let {
            map += BitmapLocation.IN_RIGHT to it.conveyorModule
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing.rotateYCCW()))?.let {
            map += BitmapLocation.IN_LEFT to it.conveyorModule
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing))?.let {
            when (it.facing) {
                facing -> map += BitmapLocation.OUT_FRONT to it.conveyorModule
                facing.opposite -> map += BitmapLocation.OUT_BACK to it.conveyorModule
                facing.rotateY() -> map += BitmapLocation.OUT_RIGHT to it.conveyorModule
                facing.rotateYCCW() -> map += BitmapLocation.OUT_LEFT to it.conveyorModule
                else -> Unit
            }
        }
        return map
    }

    private fun checkCollision(thisBitMap: IBitMap, boxedItem: BoxedItem, speed: Float): Boolean {
        val thisTemp = thisBitMap.copy()
        val hitbox = boxedItem.getHitBox()

        thisTemp.unmark(hitbox)

        boxedItem.move(speed)
        val newHitbox = boxedItem.getHitBox()

        val tempResult = !thisTemp.test(newHitbox)
        boxedItem.move(-speed)
        return tempResult
    }

    fun addItem(stack: ItemStack): Boolean {
        val route = if (flip) {
            Route.LEFT_FORWARD
        } else {
            Route.RIGHT_FORWARD
        }
        flip = !flip
        val box = BoxedItem(stack.copy(), 2f, route, world.totalWorldTime)
        val bitmap = BitMap().apply { boxes.forEach { mark(it.getHitBox()) } }

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
        val box = BoxedItem(stack.copy(), 0f, newRoute, world.totalWorldTime)
        val bitmap = BitMap().apply { boxes.forEach { mark(it.getHitBox()) } }

        if (bitmap.test(box.getHitBox())) {
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
            val lastBox = boxes.maxBy { it.position } ?: return ItemStack.EMPTY
            boxes.remove(lastBox)
            container.sendUpdateToNearPlayers()
            return lastBox.item
        }
        return ItemStack.EMPTY
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        newBoxes.clear()
        newBoxes += nbt.getList("items")
                .map { it as NBTTagCompound }
                .map { BoxedItem(it) }
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
        boxes.forEach {
            world.dropItem(it.item, pos)
        }
        boxes.clear()
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        if (args.heldItem.isNotEmpty) {
            val item = args.heldItem.item
            if (item is ItemBlockBase && item.blockBase == AutomaticMachines.conveyorBelt) {
                return false
            }
            val success = addItem(args.heldItem)
            if (success && world.isServer) {
                args.playerIn.setHeldItem(args.hand, ItemStack.EMPTY)
            }
            return true
        } else {
            val item = removeItem()
            if (item.isNotEmpty) {
                if (world.isServer) {
                    args.playerIn.inventory.addItemStackToInventory(item)
                }
                return true
            }
        }
        return false
    }
}