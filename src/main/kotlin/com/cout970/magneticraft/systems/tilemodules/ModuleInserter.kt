package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.features.items.Upgrades
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.decodeFlags
import com.cout970.magneticraft.misc.encodeFlags
import com.cout970.magneticraft.misc.inventory.*
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.fromEntity
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.systems.gui.DATA_ID_FLAGS
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityMinecartContainer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.oredict.OreDictionary


/**
 * Created by cout970 on 2017/06/20.
 */

enum class Level { LOW, HIGH }

enum class State {
    CONTRACTED,
    CONTRACTED_INVERSE,
    EXTENDED_LOW,
    EXTENDED_HIGH,
    EXTENDED_LOW_INVERSE,
    EXTENDED_HIGH_INVERSE,
    TRANSITION
}

enum class Transition(val animation: String) {
    MOVE_TO_DROP_LOW("animation0"),
    ROTATING("animation1"),
    ROTATING_INVERSE("animation2"),
    MOVE_FROM_DROP_LOW("animation3"),
    MOVE_TO_DROP_LOW_INVERSE("animation4"),
    MOVE_FROM_DROP_LOW_INVERSE("animation5"),
    MOVE_TO_DROP_HIGH("animation6"),
    MOVE_FROM_DROP_HIGH("animation7"),
    MOVE_TO_DROP_HIGH_INVERSE("animation8"),
    MOVE_FROM_DROP_HIGH_INVERSE("animation9"),
}

class ModuleInserter(
    val facingGetter: () -> EnumFacing,
    val inventory: Inventory,
    val filters: Inventory,
    override val name: String = "module_conveyor_belt" // TODO fix for 1.14
) : IModule {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()

    var hasSpeedUpgrade = false
    var hasStackUpgrade = false

    var canGrabItems = false
    var canDropItems = false
    var whiteList = false
    var useOreDictionary = false
    var useMetadata = true
    var useNbt = true

    val delay: Float get() = if (hasSpeedUpgrade) 5f else 10f
    val maxStackSize: Int get() = if (hasStackUpgrade) 64 else 8
    val moving: Boolean get() = state == State.TRANSITION

    var state = State.CONTRACTED
    var transition = Transition.MOVE_TO_DROP_LOW
    var animationTime = 0f
    var maxAnimationTime = delay
    var sleep = 0

    //@formatter:off
    var currentItem: ItemStack
        get() = inventory[0]
        set(i) { inventory[0] = i }
    //@formatter:on

    override fun init() {
        updateUpgrades()
    }

    override fun update() {

        if (sleep > 0) {
            sleep--
            return
        }
        if (moving && animationTime < maxAnimationTime) animationTime += 1f

        if (state == State.TRANSITION && animationTime >= maxAnimationTime) {
            state = when (transition) {
                Transition.MOVE_TO_DROP_LOW -> State.EXTENDED_LOW
                Transition.ROTATING -> State.CONTRACTED_INVERSE
                Transition.ROTATING_INVERSE -> State.CONTRACTED
                Transition.MOVE_FROM_DROP_LOW -> State.CONTRACTED
                Transition.MOVE_TO_DROP_LOW_INVERSE -> State.EXTENDED_LOW_INVERSE
                Transition.MOVE_FROM_DROP_LOW_INVERSE -> State.CONTRACTED_INVERSE
                Transition.MOVE_TO_DROP_HIGH -> State.EXTENDED_HIGH
                Transition.MOVE_FROM_DROP_HIGH -> State.CONTRACTED
                Transition.MOVE_TO_DROP_HIGH_INVERSE -> State.EXTENDED_HIGH_INVERSE
                Transition.MOVE_FROM_DROP_HIGH_INVERSE -> State.CONTRACTED_INVERSE
            }
        }

        when (state) {
            State.CONTRACTED -> {
                if (shouldGrabItems()) {
                    if (canGrabFromInventory(Level.HIGH)) {
                        transition(Transition.MOVE_TO_DROP_HIGH)
                    } else if (canGrabFromInventory(Level.LOW)) {
                        transition(Transition.MOVE_TO_DROP_LOW)
                    } else if (canGrabItems && canGrabFromGround(Level.LOW)) {
                        transition(Transition.MOVE_TO_DROP_LOW)
                    } else if (canGrabItems && canGrabFromGround(Level.HIGH)) {
                        transition(Transition.MOVE_TO_DROP_HIGH)
                    }
                } else if (shouldDropItems()) {
                    transition(Transition.ROTATING)
                }
            }
            State.CONTRACTED_INVERSE -> {
                if (shouldDropItems()) {
                    if (canDropToInventory(Level.HIGH)) {
                        transition(Transition.MOVE_TO_DROP_HIGH_INVERSE)
                    } else if (canDropToInventory(Level.LOW)) {
                        transition(Transition.MOVE_TO_DROP_LOW_INVERSE)
                    } else if (canDropItems && canDropToGround(Level.LOW)) {
                        transition(Transition.MOVE_TO_DROP_LOW_INVERSE)
                    } else if (canDropItems && canDropToGround(Level.HIGH)) {
                        transition(Transition.MOVE_TO_DROP_HIGH_INVERSE)
                    }
                } else if (shouldGrabItems()) {
                    transition(Transition.ROTATING_INVERSE)
                }
            }
            State.EXTENDED_LOW -> if (canGrab(Level.LOW) && grab(Level.LOW)) transition(Transition.MOVE_FROM_DROP_LOW) else {
                if (shouldRetractInsteadOfGrab(Level.LOW)) transition(Transition.MOVE_FROM_DROP_LOW) else sleep()
            }
            State.EXTENDED_HIGH -> if (canGrab(Level.HIGH) && grab(Level.HIGH)) transition(Transition.MOVE_FROM_DROP_HIGH) else {
                if (shouldRetractInsteadOfGrab(Level.LOW)) transition(Transition.MOVE_FROM_DROP_HIGH) else sleep()
            }
            State.EXTENDED_LOW_INVERSE -> if (canDrop(Level.LOW) && drop(Level.LOW)) transition(Transition.MOVE_FROM_DROP_LOW_INVERSE) else {
                if (shouldRetractInsteadOfDrop(Level.LOW)) transition(Transition.MOVE_FROM_DROP_LOW_INVERSE) else sleep()
            }
            State.EXTENDED_HIGH_INVERSE -> if (canDrop(Level.HIGH) && drop(Level.HIGH)) transition(Transition.MOVE_FROM_DROP_HIGH_INVERSE) else {
                if (shouldRetractInsteadOfDrop(Level.LOW)) transition(Transition.MOVE_FROM_DROP_HIGH_INVERSE) else sleep()
            }
            else -> Unit
        }
    }

    fun transition(new: Transition) {
        state = State.TRANSITION
        transition = new
        maxAnimationTime = delay
        animationTime = 0f
        container.sendUpdateToNearPlayers()
    }

    fun sleep() {
        sleep = delay.toInt()
        container.sendUpdateToNearPlayers()
    }

    fun updateUpgrades() {

        hasSpeedUpgrade = false
        hasStackUpgrade = false

        for (i in 1..2) {
            val stack = inventory[i]
            if (stack.isEmpty || stack.item != Upgrades.inserterUpgrade) continue

            when (stack.metadata) {
                0 -> hasSpeedUpgrade = true
                1 -> hasStackUpgrade = true
            }
        }
    }

    fun shouldGrabItems() = currentItem.isEmpty
    fun shouldDropItems() = currentItem.isNotEmpty

    private fun getInv(level: Level, inverse: Boolean): IItemHandler? {
        val inventoryAccessors = mutableListOf<Pair<BlockPos, EnumFacing>>()
        val facing = if (inverse) facing.opposite else facing

        if (level == Level.LOW) {
            inventoryAccessors += (facing.toBlockPos() + EnumFacing.DOWN) to EnumFacing.UP
            inventoryAccessors += facing.toBlockPos() to facing.opposite
        } else {
            inventoryAccessors += facing.toBlockPos() to EnumFacing.UP
        }

        inventoryAccessors.forEach { (offset, side) ->
            val base = pos + offset
            val area = base createAABBUsing base.add(1, 1, 1)
            val carts = world.getEntitiesWithinAABB(EntityMinecartContainer::class.java, area)

            carts.forEach { entity ->
                val handler = ITEM_HANDLER!!.fromEntity(entity, side)
                if (handler != null) return handler
            }

            val tile = world.getTileEntity(pos + offset) ?: return@forEach
            val handler = ITEM_HANDLER!!.fromTile(tile, side)
            if (handler != null) return handler
        }

        return null
    }

    private fun getDropInv(): IItemHandler? {
        Level.values().forEach {
            val handler = getInv(it, true)
            if (handler != null) return handler
        }
        return null
    }

    fun canGrab(level: Level): Boolean = canGrabFromInventory(level) || (canGrabItems && canGrabFromGround(level))

    fun canGrabFromInventory(level: Level): Boolean {
        if (shouldDropItems()) return false
        val handler = getInv(level, false) ?: return false

        handler.forEachIndexed { slot, _ ->
            val stack = handler.extractItem(slot, maxStackSize, true)
            if (stack.isEmpty) return@forEachIndexed
            if (!canExtract(stack)) return@forEachIndexed
            val accepted = acceptInDestine(stack)
            if (accepted != null) return true
        }
        return false
    }

    fun canGrabFromGround(level: Level): Boolean {
        val height = if (level == Level.HIGH) pos else pos.down()
        val base = height.toVec3d() + facing.toVector3()
        val area = base createAABBUsing base.addVector(1.0, 1.0, 1.0)
        val items = world.getEntitiesWithinAABB(EntityItem::class.java, area)
        return items.any { canExtract(it.item) && acceptInDestine(it.item) != null }
    }

    fun grab(level: Level): Boolean = grabFromInventory(level) || (canGrabItems && grabFromGround(level))

    fun grabFromInventory(level: Level): Boolean {
        if (world.isClient) return false
        val handler = getInv(level, false) ?: return false

        handler.forEachIndexed { slot, _ ->
            val stack = handler.extractItem(slot, maxStackSize, true)
            if (stack.isEmpty) return@forEachIndexed
            if (!canExtract(stack)) return@forEachIndexed
            val accepted = acceptInDestine(stack)
            if (accepted != null) {
                currentItem = handler.extractItem(slot, accepted, false)
                return true
            }
        }
        return false
    }

    fun grabFromGround(level: Level): Boolean {
        if (world.isClient) return false
        val height = if (level == Level.HIGH) pos else pos.down()
        val base = height.toVec3d() + facing.toVector3()
        val area = base createAABBUsing base.addVector(1.0, 1.0, 1.0)
        val items = world.getEntitiesWithinAABB(EntityItem::class.java, area)
        val stack = items.firstOrNull { canExtract(it.item) && acceptInDestine(it.item) != null } ?: return false

        currentItem = stack.item
        stack.item = ItemStack.EMPTY
        world.removeEntity(stack)
        return true
    }

    fun canDrop(level: Level): Boolean = canDropToInventory(level) || (canDropItems && canDropToGround(level))

    fun canDropToInventory(level: Level): Boolean {
        if (shouldGrabItems()) return false
        val handler = getInv(level, true) ?: return false

        for (slot in 0 until handler.slots) {
            val remaining = handler.insertItem(slot, currentItem, true)
            if (!ItemStack.areItemStacksEqual(remaining, currentItem)) return true
        }
        return false
    }

    fun canDropToGround(level: Level): Boolean {
        val height = if (level == Level.HIGH) pos else pos.down()
        val base = height + facing.opposite

        val blockstate = world.getBlockState(base)
        if (blockstate.isFullBlock) return false

        val area = base createAABBUsing base.add(1.0, 1.0, 1.0)
        val itemList = world.getEntitiesWithinAABB(EntityItem::class.java, area)
        return itemList.isEmpty()
    }

    fun drop(level: Level): Boolean = dropToInventory(level) || (canDropItems && dropToGround(level))

    fun dropToInventory(level: Level): Boolean {
        if (world.isClient) return false
        val handler = getInv(level, true) ?: return false

        val remaining = handler.insertItem(currentItem, true)
        if (ItemStack.areItemStacksEqual(remaining, currentItem)) return false

        currentItem = handler.insertItem(currentItem, false)
        return true
    }

    fun dropToGround(level: Level): Boolean {
        if (world.isClient) return true
        val height = if (level == Level.HIGH) pos else pos.down()
        world.dropItem(currentItem, height + facing.opposite, false)
        currentItem = ItemStack.EMPTY
        return true
    }

    fun acceptInDestine(stack: ItemStack): Int? {
        if (stack.isEmpty) return null
        if (canDropItems) return maxStackSize
        val other = getDropInv() ?: return null
        val remaining = other.insertItem(stack, true)

        val accepted = if (remaining.isEmpty) stack.count else stack.count - remaining.count
        if (accepted <= 0) return null

        return accepted
    }

    fun shouldRetractInsteadOfGrab(level: Level): Boolean {
        if (world.isClient) return false
        if (shouldDropItems()) return true
        return getInv(level, false) == null
    }

    fun shouldRetractInsteadOfDrop(level: Level): Boolean {
        if (world.isClient) return false
        if (shouldGrabItems()) return true
        return getInv(level, true) == null
    }

    fun canExtract(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        repeat(filters.slots) { slot ->
            if (checkFilter(filters[slot], stack)) return whiteList
        }
        return !whiteList
    }

    fun checkFilter(filter: ItemStack, stack: ItemStack): Boolean {
        if (filter.isEmpty) return false

        if (useOreDictionary && filter.item != stack.item) {
            val filterIds = OreDictionary.getOreIDs(filter)
            val stackIds = OreDictionary.getOreIDs(stack)
            if (stackIds.isNotEmpty() && filterIds.isNotEmpty()) {
                for (stackId in stackIds) {
                    for (filterId in filterIds) {
                        if (stackId == filterId) return true
                    }
                }
            }
        }

        if (filter.item !== stack.item) return false
        if (useMetadata && filter.itemDamage != stack.itemDamage) return false
        if (useNbt && filter.tagCompound != stack.tagCompound) return false
        return true
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        state = State.values()[nbt.getInteger("state")]
        transition = Transition.values()[nbt.getInteger("transition")]
        animationTime = nbt.getFloat("animationTime")
        maxAnimationTime = nbt.getFloat("maxAnimationTime")
        sleep = nbt.getInteger("sleep")
        filters.deserializeNBT(nbt.getCompoundTag("filters"))
        whiteList = nbt.getBoolean("whiteList")
        useOreDictionary = nbt.getBoolean("useOreDictionary")
        useMetadata = nbt.getBoolean("useMetadata")
        useNbt = nbt.getBoolean("useNbt")
        canDropItems = nbt.getBoolean("canDropItems")
        canGrabItems = nbt.getBoolean("canGrabItems")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("state", state.ordinal)
        add("transition", transition.ordinal)
        add("animationTime", animationTime)
        add("maxAnimationTime", maxAnimationTime)
        add("sleep", sleep)
        add("filters", filters.serializeNBT())
        add("whiteList", whiteList)
        add("useOreDictionary", useOreDictionary)
        add("useMetadata", useMetadata)
        add("useNbt", useNbt)
        add("canDropItems", canDropItems)
        add("canGrabItems", canGrabItems)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
        IntSyncVariable(DATA_ID_FLAGS,
            { encodeFlags(whiteList, useOreDictionary, useMetadata, useNbt, canDropItems, canGrabItems) },
            {
                val flags = decodeFlags(it, 6)
                whiteList = flags[0]
                useOreDictionary = flags[1]
                useMetadata = flags[2]
                useNbt = flags[3]
                canDropItems = flags[4]
                canGrabItems = flags[5]
            })
    )
}