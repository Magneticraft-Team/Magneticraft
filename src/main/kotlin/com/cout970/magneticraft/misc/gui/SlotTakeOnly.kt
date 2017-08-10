package com.cout970.magneticraft.misc.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/07/01.
 */
open class SlotTakeOnly(inv: IItemHandler, index: Int, x: Int, y: Int) : SlotItemHandler(inv, index, x, y) {
    override fun isItemValid(stack: ItemStack): Boolean {
        return false
    }

    override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
        return true
    }
}