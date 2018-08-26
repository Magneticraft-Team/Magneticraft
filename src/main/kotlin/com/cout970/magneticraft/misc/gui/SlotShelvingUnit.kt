package com.cout970.magneticraft.misc.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/07/29.
 */
class SlotShelvingUnit(inv: IItemHandler, id: Int, x: Int, y: Int) : SlotItemHandler(inv, id, x, y) {

    companion object {
        const val HIDE_OFFSET = 1 shl 20
    }

    var locked = false
    var hidden = false

    fun hide() {
        if (!hidden) {
            hidden = true
            xPos += HIDE_OFFSET
            yPos += HIDE_OFFSET
        }
    }

    fun show() {
        if (hidden) {
            hidden = false
            xPos -= HIDE_OFFSET
            yPos -= HIDE_OFFSET
        }
    }

    fun lock() {
        locked = true
    }

    fun unlock() {
        locked = false
    }

    override fun canTakeStack(playerIn: EntityPlayer): Boolean {
        return !locked && super.canTakeStack(playerIn)
    }

    override fun isItemValid(stack: ItemStack): Boolean {
        return !locked && super.isItemValid(stack)
    }

    override fun getSlotStackLimit(): Int {
        return if (locked) 0 else super.getSlotStackLimit()
    }
}