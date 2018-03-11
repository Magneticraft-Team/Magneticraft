package com.cout970.magneticraft.misc.gui

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class SlotUnmodifiableItemHandler(inv: IItemHandler, index: Int, x: Int, y: Int) : SlotItemHandler(inv, index, x, y) {

    override fun putStack(stack: ItemStack) {
        if (stack.isEmpty) {
            itemHandler.extractItem(slotIndex, 64, false)
        } else {
            itemHandler.insertItem(slotIndex, stack, false)
        }
    }
}