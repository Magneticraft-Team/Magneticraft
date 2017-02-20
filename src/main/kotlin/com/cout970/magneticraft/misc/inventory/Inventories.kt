package com.cout970.magneticraft.misc.inventory

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by cout970 on 07/07/2016.
 */

fun Item.stack(size: Int = 1, meta: Int = 0) = ItemStack(this, size, meta)

fun ItemStack.consumeItem(amount: Int = 1): ItemStack? {
    if (stackSize > amount) {
        stackSize -= amount
        return this
    } else {
        return item.getContainerItem(this)
    }
}

operator fun IItemHandlerModifiable.set(slot: Int, stack: ItemStack?): Unit {
    setStackInSlot(slot, stack)
}
operator fun IItemHandler.get(slot: Int): ItemStack? {
    return getStackInSlot(slot)
}
