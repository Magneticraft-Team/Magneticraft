package com.cout970.magneticraft.misc.inventory

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by cout970 on 07/07/2016.
 */

fun Item.stack(size: Int = 1, meta: Int = 0) = ItemStack(this, size, meta)
fun Block.stack(size: Int = 1, meta: Int = 0) = ItemStack(this, size, meta)

fun ItemStack.consumeItem(amount: Int = 1): ItemStack {
    if (count > amount) {
        count -= amount
        return this
    } else {
        return item.getContainerItem(this)
    }
}

operator fun IItemHandlerModifiable.set(slot: Int, stack: ItemStack): Unit {
    setStackInSlot(slot, stack)
}

operator fun IItemHandler.get(slot: Int): ItemStack {
    return getStackInSlot(slot)
}

@Suppress("LoopToCallChain")
inline fun IItemHandler.forEach(func: (ItemStack) -> Unit) {
    for(index in 0 until slots){
        val stack = getStackInSlot(index)
        if(stack.isNotEmpty){
            func(stack)
        }
    }
}
inline fun IItemHandler.forEachIndexed(func: (Int, ItemStack) -> Unit) {
    for(index in 0 until slots){
        val stack = getStackInSlot(index)
        if(stack.isNotEmpty){
            func(index, stack)
        }
    }
}

val ItemStack.isNotEmpty get() = !isEmpty

fun ItemStack.withSize(size: Int) = ItemStack(item, size, itemDamage, tagCompound)