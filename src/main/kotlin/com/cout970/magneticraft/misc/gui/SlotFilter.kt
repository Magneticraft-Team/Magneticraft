package com.cout970.magneticraft.misc.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler


class SlotLegacyFilter(inv: IInventory, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {

    override fun getItemStackLimit(stack: ItemStack): Int = 1

    override fun isItemValid(p_75214_1_: ItemStack): Boolean = true

    override fun canTakeStack(playerIn: EntityPlayer): Boolean = true
}

class SlotFabricator(inv: IItemHandler, index: Int, x: Int, y: Int) : SlotItemHandler(inv, index, x, y) {

    override fun getItemStackLimit(stack: ItemStack): Int = 0

    override fun isItemValid(p_75214_1_: ItemStack): Boolean = false

    override fun canTakeStack(playerIn: EntityPlayer): Boolean = false
}