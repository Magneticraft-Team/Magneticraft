package com.cout970.magneticraft.misc.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.SlotItemHandler

open class TypedSlot(
    inv: IItemHandler,
    index: Int,
    x: Int,
    y: Int,
    val type: SlotType = SlotType.NORMAL
) : SlotItemHandler(inv, index, x, y) {

    override fun putStack(stack: ItemStack) {
        if (itemHandler is IItemHandlerModifiable) {
            super.putStack(stack)
        } else {
            if (stack.isEmpty) {
                itemHandler.extractItem(slotIndex, 64, false)
            } else {
                itemHandler.insertItem(slotIndex, stack, false)
            }
            onSlotChanged()
        }
    }
}

enum class SlotType {
    NORMAL, INPUT, OUTPUT, FILTER, BUTTON, FLOPPY, BATTERY
}

/**
 * Created by cout970 on 2017/07/01.
 */
open class SlotTakeOnly(inv: IItemHandler, index: Int, x: Int, y: Int) : TypedSlot(inv, index, x, y, SlotType.OUTPUT) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return false
    }

    override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
        return true
    }
}

class SlotFilter(inv: IItemHandler, index: Int, x: Int, y: Int) : TypedSlot(inv, index, x, y, SlotType.FILTER) {

    override fun getItemStackLimit(stack: ItemStack): Int = 1
    override fun isItemValid(p_75214_1_: ItemStack): Boolean = true
    override fun canTakeStack(playerIn: EntityPlayer): Boolean = true
}

class SlotButton(inv: IItemHandler, index: Int, x: Int, y: Int, val onClick: (EntityPlayer, Int) -> Unit) : TypedSlot(inv, index, x, y, SlotType.BUTTON) {

    override fun getItemStackLimit(stack: ItemStack): Int = 0
    override fun isItemValid(p_75214_1_: ItemStack): Boolean = false
    override fun canTakeStack(playerIn: EntityPlayer): Boolean = false
}