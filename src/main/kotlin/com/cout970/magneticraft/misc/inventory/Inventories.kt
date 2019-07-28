package com.cout970.magneticraft.misc.inventory

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.systems.blocks.BlockBase
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.*

/**
 * Created by cout970 on 07/07/2016.
 */

fun Item.stack(size: Int = 1, meta: Int = 0) = ItemStack(this, size, meta)

fun Block.stack(size: Int = 1, meta: Int = 0) = ItemStack(this, size, meta)

fun IBlockState.stack(size: Int = 1): ItemStack {
    val block = block

    return when {
        block is IFluidBlock -> {
            if (block.getMetaFromState(this) != 0) {
                ItemStack.EMPTY
            } else {
                FluidUtil.getFilledBucket(block.fluid.stack())
            }
        }
        Item.getItemFromBlock(block) == Items.AIR -> {
            ItemStack(block, size)
        }
        (block as? BlockBase)?.alwaysDropDefault == true -> {
            ItemStack(block.getItemDropped(this, Random(), 0), size, block.getMetaFromState(this))
        }
        else -> {
            ItemStack(block.getItemDropped(this, Random(), 0), size, block.damageDropped(this))
        }
    }
}

fun Fluid.stack() = FluidStack(this, 1000)

@Suppress("DEPRECATION")
fun ItemStack.toBlockState(): IBlockState? {
    val itemBlock = item as? ItemBlock ?: return null
    return itemBlock.block.getStateFromMeta(metadata)
}

operator fun IItemHandlerModifiable.set(slot: Int, stack: ItemStack) {
    setStackInSlot(slot, stack)
}

operator fun IItemHandler.get(slot: Int): ItemStack {
    return getStackInSlot(slot)
}

@Suppress("LoopToCallChain")
inline fun IItemHandler.forEach(func: (ItemStack) -> Unit) {
    for (index in 0 until slots) {
        val stack = getStackInSlot(index)
        if (stack.isNotEmpty) {
            func(stack)
        }
    }
}

inline fun IItemHandler.forEachIndexed(func: (Int, ItemStack) -> Unit) {
    for (index in 0 until slots) {
        val stack = getStackInSlot(index)
        if (stack.isNotEmpty) {
            func(index, stack)
        }
    }
}

val ItemStack.isNotEmpty get() = !isEmpty

fun ItemStack.withSize(size: Int): ItemStack = this.copy().also { it.count = size }

fun IItemHandler.isEmpty(): Boolean {
    forEach { stack ->
        if (stack.isNotEmpty) return false
    }
    return true
}

fun IItemHandler.copy(): IItemHandler {
    val inv = Inventory(slots)
    forEachIndexed { index, stack ->
        inv.setStackInSlot(index, stack.copy())
    }
    return inv
}

fun IItemHandler.getSlotForExtraction(other: IItemHandler): Int? {
    for (slot in 0 until slots) {
        val stack = this.extractItem(slot, 64, true)
        if (stack.isEmpty) continue
        val result = other.insertItem(stack, true)
        if (ItemStack.areItemStacksEqual(result, stack)) continue
        return slot
    }
    return null
}

fun IItemHandler.insertItem(stack: ItemStack, simulate: Boolean): ItemStack {
    var remaining = stack
    var preferredSlot = -1

    for (slot in 0 until slots) {
        if (!ApiUtils.equalsIgnoreSize(getStackInSlot(slot), stack)) continue
        if (insertItem(slot, stack, true).count != stack.count) {
            preferredSlot = slot
            break
        }
    }

    if (preferredSlot >= 0) {
        remaining = insertItem(preferredSlot, remaining, simulate)
        if (remaining.isEmpty) return ItemStack.EMPTY
    }

    for (it in 0 until slots) {
        remaining = insertItem(it, remaining, simulate)
        if (remaining.isEmpty) return ItemStack.EMPTY
    }
    return remaining
}

fun IItemHandler.insertAll(list: List<ItemStack>) {
    list.forEach { insertItem(it, false) }
}

fun IItemHandler.canAcceptAll(items: List<ItemStack>): Boolean {
    val accumulator = copy()
    items.forEach {
        if (insertItem(it, true).isNotEmpty) return false
        if (accumulator.insertItem(it, false).isNotEmpty) return false
    }
    return true
}