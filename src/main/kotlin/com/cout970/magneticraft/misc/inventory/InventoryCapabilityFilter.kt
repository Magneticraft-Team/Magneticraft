package com.cout970.magneticraft.misc.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

/**
 * Created by cout970 on 2017/07/01.
 */
class InventoryCapabilityFilter(
        val inventory: IItemHandler,
        val inputSlots: List<Int>,
        val outputSlots: List<Int>
) : IItemHandler {

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (toRealSlot(slot) in inputSlots) {
            return inventory.insertItem(toRealSlot(slot), stack, simulate)
        }
        return stack
    }

    override fun getStackInSlot(slot: Int): ItemStack = inventory.getStackInSlot(toRealSlot(slot))

    override fun getSlotLimit(slot: Int): Int = inventory.getSlotLimit(toRealSlot(slot))

    override fun getSlots(): Int = inputSlots.size + outputSlots.size

    fun toRealSlot(index: Int): Int {
        if (index < inputSlots.size) {
            return inputSlots[index]
        } else {
            return outputSlots[index - inputSlots.size]
        }
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (toRealSlot(slot) in outputSlots) {
            return inventory.extractItem(toRealSlot(slot), amount, simulate)
        }
        return ItemStack.EMPTY
    }
}