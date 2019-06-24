package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class GuiBuilder(val container: JsonContainer) {

    fun slot(inventory: IItemHandler, index: Int) {
        container.addSlotToContainer(SlotItemHandler(inventory, index, 0, 0))
    }

    fun region(
        first: Int,
        size: Int,
        inverseDirection: Boolean = false,
        filter: (ItemStack, Int) -> Boolean = { _, _ -> true }
    ) {
        container.inventoryRegions += InventoryRegion(first until first + size, inverseDirection, filter)
    }

    fun verticalBar(name: String, node: IElectricNode){
        container.guiComponents[name] = GuiComponentElectricNode(node)
    }
}