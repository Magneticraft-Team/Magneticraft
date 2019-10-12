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

    val slotMap = generateSlotMap()

    private fun generateSlotMap(): List<Int> {
        val map = mutableListOf<Int>()

        inputSlots.forEach { slot ->
            if (slot !in map)
                map.add(slot)
        }

        outputSlots.forEach { slot ->
            if (slot !in map)
                map.add(slot)
        }

        return map
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val realSlot = toRealSlot(slot)
        if (realSlot in inputSlots) {
            return inventory.insertItem(realSlot, stack, simulate)
        }
        return stack
    }

    override fun getStackInSlot(slot: Int): ItemStack = inventory.getStackInSlot(toRealSlot(slot))

    override fun getSlotLimit(slot: Int): Int = inventory.getSlotLimit(toRealSlot(slot))

    override fun getSlots(): Int = slotMap.size

    fun toRealSlot(index: Int): Int = slotMap[index]

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val realSlot = toRealSlot(slot)
        if (realSlot in outputSlots) {
            return inventory.extractItem(realSlot, amount, simulate)
        }
        return ItemStack.EMPTY
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryCapabilityFilter

        if (inventory != other.inventory) return false
        if (inputSlots != other.inputSlots) return false
        if (outputSlots != other.outputSlots) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inventory.hashCode()
        result = 31 * result + inputSlots.hashCode()
        result = 31 * result + outputSlots.hashCode()
        return result
    }

    override fun toString(): String {
        return "InventoryCapabilityFilter(inventory=$inventory, inputSlots=$inputSlots, outputSlots=$outputSlots, slotMap=$slotMap)"
    }
}