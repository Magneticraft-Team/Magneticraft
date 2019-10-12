package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.conveyorbelt.IConveyorBelt
import com.cout970.magneticraft.api.conveyorbelt.Route
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.features.automatic_machines.Blocks
import com.cout970.magneticraft.misc.getList
import com.cout970.magneticraft.misc.inventory.insertItem
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.withSize
import com.cout970.magneticraft.misc.list
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.TimeCache
import com.cout970.magneticraft.misc.tileentity.getCap
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.times
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.CONVEYOR_BELT
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.itemblocks.ItemBlockBase
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilemodules.conveyorbelt.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler

/**
 * Created by cout970 on 2017/06/17.
 */
class ModuleConveyorBelt(
    val facingGetter: () -> EnumFacing,
    val hardLimit: Float? = null,
    val frontOffset: Int? = null,
    override val name: String = "module_conveyor_belt"
) : IModule, IOnActivated, IConveyorBelt {

    override lateinit var container: IModuleContainer

    val tileRefCallback = object : ITileRef {
        override fun getWorld(): World = container.world
        override fun getPos(): BlockPos = container.pos
    }

    override fun getFacing(): EnumFacing = facingGetter()

    private val boxes = mutableListOf<BoxedItem>()
    var toUpdate = false
    private val newBoxes = mutableListOf<BoxedItem>()
    private var flip = true
    private var needsSync = true

    override fun getBoxes(): MutableList<BoxedItem> = boxes

    override fun getLevel(): Int = when {
        isUp -> 1
        isDown -> -1
        else -> 0
    }

    fun generateGlobalBitMap(): IBitMap {
        val map = getSurroundMap()
        val bitmap = FullBitMap(BitMap(), map.mapValues { BitMap() })

        boxes.forEach { bitmap.mark(it.getHitBox()) }

        map.forEach { (pos, mod) ->
            mod.boxes.forEach {
                bitmap.mark(pos.fromExternalToLocal(it.getHitBox()))
            }
        }
        return bitmap
    }

    val hasBack = TimeCache(tileRefCallback, 10) {
        world.getCap(CONVEYOR_BELT!!, pos.add(facing.opposite.directionVec), null)?.facing == facing
    }

    val hasFront = TimeCache(tileRefCallback, 10) {
        world.getCap(CONVEYOR_BELT!!, pos.add(facing.directionVec), null) != null
    }

    val hasLeft = TimeCache(tileRefCallback, 10) {
        world.getCap(CONVEYOR_BELT!!, pos.add(facing.rotateYCCW().directionVec), null)?.facing == facing.rotateY()
    }

    val hasRight = TimeCache(tileRefCallback, 10) {
        world.getCap(CONVEYOR_BELT!!, pos.add(facing.rotateY().directionVec), null)?.facing == facing.rotateYCCW()
    }

    val hasFrontUp = TimeCache(tileRefCallback, 10) {
        world.getCap(CONVEYOR_BELT!!, pos + facing + EnumFacing.UP, null)?.facing == facing
    }

    val hasBackUp = TimeCache(tileRefCallback, 10) {
        world.getCap(CONVEYOR_BELT!!, pos + facing.opposite + EnumFacing.UP, null)?.facing == facing
    }

    val hasBlockUp = TimeCache(tileRefCallback, 10) {
        val state = world.getBlockState(pos + EnumFacing.UP)
        !state.block.isAir(state, world, pos + EnumFacing.UP)
    }

    val hasBlockFront = TimeCache(tileRefCallback, 10) {
        val state = world.getBlockState(pos + facing)
        !state.block.isAir(state, world, pos + facing)
    }

    val hasBlockDown = TimeCache(tileRefCallback, 10) {
        world.isSideSolid(pos + EnumFacing.DOWN, EnumFacing.UP)
    }

    val isCorner
        get() = !hasBack() && hasFront() && (hasLeft() xor hasRight())

    val isDown
        get() = !hasBack() && hasBackUp() && !hasBlockUp()

    val isUp
        get() = !hasFront() && hasFrontUp() && !hasBlockUp()

    override fun update() {
        advanceSimulation()
    }

    fun advanceSimulation() {
        // debug
//        if (world.isServer || world.totalWorldTime % 20 != 0L) return

        // Load data generated at deserializeNBT now that the world is set
        if (toUpdate) {
            boxes.clear()
            boxes.addAll(newBoxes)
            newBoxes.clear()
            toUpdate = false
        }

        val tile = frontTile()
        val frontInv = tile?.let { ITEM_HANDLER!!.fromTile(it, facing.opposite) }
        val frontBelt = tile?.let { CONVEYOR_BELT!!.fromTile(it, facing.opposite) }

        // if there is no block in front don't go all the way
        val limit = hardLimit ?: if (frontBelt != null && canInsertIntoBelt(frontBelt)) 15f else 13f

        advanceItems(limit)

        // Move to next belt
        if (frontBelt != null) {
            moveToNextBelt(frontBelt, limit)
        } else if (frontInv != null) {
            moveToInventory(frontInv, limit)
        }

        if (needsSync && world.isServer) {
            container.sendUpdateToNearPlayers()
            needsSync = false
        }
    }

    fun canInsertIntoBelt(frontBelt: IConveyorBelt): Boolean {
        if (frontBelt.facing.opposite == facing) return false
        return (frontBelt.level == 0) || frontBelt.facing == facing
    }

    fun moveToNextBelt(frontBelt: IConveyorBelt, limit: Float) {
        if (canInsertIntoBelt(frontBelt)) {
            val removed = boxes.removeAll {
                if (it.position <= limit) return@removeAll false
                frontBelt.addItem(it.item, facing, it.route)
            }
            if (removed) {
                markUpdate()
            }
        }
    }

    fun moveToInventory(inv: IItemHandler, limit: Float) {
        val removed = boxes.removeAll {
            if (it.position <= limit)
                return@removeAll false

            if (inv.insertItem(it.item, true).isEmpty) {
                if (world.isServer) {
                    inv.insertItem(it.item, false)
                }
                return@removeAll true
            }
            return@removeAll false
        }
        if (removed) {
            markUpdate()
        }
    }

    fun advanceItems(limit: Float) {

        val fullBitMap = generateGlobalBitMap()
        val now = world.totalWorldTime

        boxes.forEach {

            if (it.lastTick != now) {
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
                it.lastTick = now
            }
        }
    }

    fun frontTile(): TileEntity? {
        val frontPos = if (frontOffset != null) {
            val offset = facing.toBlockPos().times(frontOffset)
            pos + offset
        } else {
            when {
                isUp -> pos + facing + EnumFacing.UP
                hasFront() -> pos + facing
                hasBlockFront() -> pos + facing
                else -> pos + facing + EnumFacing.DOWN
            }
        }

        return world.getTileEntity(frontPos)
    }

    // Note: this cannot be called on deserializeNBT or the game will crash of StackOverflow
    // because world field is not set and the tileEntity has not been added to the world yet
    val getSurroundMap = TimeCache(tileRefCallback, 10) {
        val map = mutableMapOf<BitmapLocation, IConveyorBelt>()

        val back = when {
            isUp -> pos + facing.opposite + EnumFacing.DOWN
            isDown -> pos + facing.opposite + EnumFacing.UP
            else -> pos + facing.opposite
        }

        val front = when {
            isUp -> pos + facing + EnumFacing.DOWN
            isDown -> pos + facing + EnumFacing.UP
            else -> pos + facing
        }

        val left = pos + facing.rotateYCCW()
        val right = pos + facing.rotateY()

        world.getCap(CONVEYOR_BELT!!, back, null)?.let {
            map += BitmapLocation.IN_BACK to it
        }
        world.getCap(CONVEYOR_BELT!!, right, null)?.let {
            map += BitmapLocation.IN_RIGHT to it
        }
        world.getCap(CONVEYOR_BELT!!, left, null)?.let {
            map += BitmapLocation.IN_LEFT to it
        }
        world.getCap(CONVEYOR_BELT!!, front, null)?.let {
            if (it.level == 1 || it.level == -1) {
                if (it.facing == facing) {
                    map += BitmapLocation.OUT_FRONT to it
                }
            } else {
                when (it.facing) {
                    facing -> map += BitmapLocation.OUT_FRONT to it
                    facing.opposite -> map += BitmapLocation.OUT_BACK to it
                    facing.rotateY() -> map += BitmapLocation.OUT_RIGHT to it
                    facing.rotateYCCW() -> map += BitmapLocation.OUT_LEFT to it
                    else -> Unit
                }
            }
        }
        map
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

    override fun addItem(stack: ItemStack, simulated: Boolean): Boolean {

        val route = if (flip) Route.LEFT_FORWARD else Route.RIGHT_FORWARD

        if (!simulated)
            flip = !flip

        val box = BoxedItem(stack.copy(), 2f, route, world.totalWorldTime)
        val bitmap = BitMap().apply { boxes.forEach { mark(it.getHitBox()) } }

        val test = bitmap.test(box.getHitBox())

        if (test && !simulated) {
            boxes += box
            markUpdate()
        }

        return test
    }

    override fun addItem(stack: ItemStack, side: EnumFacing, oldRoute: Route): Boolean {
        val newRoute = getRoute(facing, side, oldRoute)
        val box = BoxedItem(stack.copy(), 0f, newRoute, world.totalWorldTime)
        val bitmap = BitMap().apply { boxes.forEach { mark(it.getHitBox()) } }

        val test = bitmap.test(box.getHitBox())

        if (test) {
            boxes += box
            markUpdate()
        }

        return test
    }

    fun getRoute(facing: EnumFacing, side: EnumFacing, oldRoute: Route): Route {

        if (facing == side) {
            return if (!oldRoute.leftSide) Route.RIGHT_FORWARD else Route.LEFT_FORWARD
        }
        if (facing.opposite == side) {
            return if (oldRoute.leftSide) Route.RIGHT_FORWARD else Route.LEFT_FORWARD
        }

        if (isCorner) {

            if (facing.rotateY() == side) {
                return if (oldRoute.leftSide) Route.LEFT_SHORT else Route.RIGHT_CORNER
            }
            if (facing.rotateYCCW() == side) {
                return if (oldRoute.leftSide) Route.LEFT_CORNER else Route.RIGHT_SHORT
            }
        } else {
            if (facing.rotateY() == side) {
                return if (oldRoute.leftSide) Route.LEFT_SHORT else Route.LEFT_LONG
            }
            if (facing.rotateYCCW() == side) {
                return if (oldRoute.leftSide) Route.RIGHT_LONG else Route.RIGHT_SHORT
            }
        }

        throw IllegalStateException("Illegal side: $side for facing: $facing")
    }


    fun removeItem(): ItemStack {
        if (boxes.isNotEmpty()) {
            val lastBox = boxes.maxBy { it.position } ?: return ItemStack.EMPTY
            boxes.remove(lastBox)
            markUpdate()
            return lastBox.item
        }
        return ItemStack.EMPTY
    }

    fun removeItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (boxes.isEmpty()) return ItemStack.EMPTY
        val lastBox = boxes[slot]

        val extracted = if (amount > lastBox.item.count) {
            lastBox.item
        } else {
            lastBox.item.withSize(amount)
        }

        if (!simulate) {
            if (amount >= lastBox.item.count) {
                boxes.remove(lastBox)
            } else {
                boxes[slot] = lastBox.copy(item = lastBox.item.withSize(lastBox.item.count - amount))
            }
            boxes.remove(lastBox)
            markUpdate()
        }
        return extracted
    }


    private fun markUpdate() {
        needsSync = true
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
            if (item is ItemBlockBase && item.blockBase == Blocks.conveyorBelt) {
                return false
            }
            val success = addItem(args.heldItem, false)
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

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {

        if (cap == CONVEYOR_BELT) return this as T
        if (cap == ITEM_HANDLER && facing == EnumFacing.UP) {
            return object : IItemHandler {

                override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                    if (slot != 0) return stack
                    return if (addItem(stack, simulate)) ItemStack.EMPTY else stack
                }

                override fun getStackInSlot(slot: Int): ItemStack =
                    if (slot == 0) ItemStack.EMPTY else boxes[slot - 1].item

                override fun getSlotLimit(slot: Int): Int = 64

                override fun getSlots(): Int = boxes.size + 1

                override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
                    if (slot == 0) ItemStack.EMPTY else removeItem(slot - 1, amount, simulate)
            } as T
        }

        return null
    }
}