package com.cout970.magneticraft.features.automatic_machines

import com.cout970.magneticraft.features.items.Upgrades
import com.cout970.magneticraft.misc.gui.SlotFilter
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.iterateArea
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.gui.components.buttons.AbstractButton
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

class ContainerInserter(val tile: TileInserter, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        val inv = tile.inventory
        addSlotToContainer(SlotItemHandler(inv, 0, 83, 50))
        addSlotToContainer(SlotItemHandler(inv, 1, 83, 14))
        addSlotToContainer(SlotItemHandler(inv, 2, 101, 14))

        var i = 0
        iterateArea(0..2, 0..2) { x, y ->
            addSlotToContainer(SlotFilter(tile.filters, i++, x * 18 + 26, y * 18 + 14))
        }

        inventoryRegions += InventoryRegion(0..0) { stack -> stack.item != Upgrades.inserterUpgrade }
        inventoryRegions += InventoryRegion(1..2) { stack -> stack.item == Upgrades.inserterUpgrade }
        inventoryRegions += InventoryRegion(3..12) { _ -> false }
        bindPlayerInventory(player.inventory)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClick(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        sendUpdate(IBD().also { it.setInteger(0, button.id) })
        return true
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getInteger(0) {
            val mod = tile.inserterModule
            when (it) {
                0 -> mod.whiteList = !mod.whiteList
                1 -> mod.useOreDictionary = !mod.useOreDictionary
                2 -> mod.useMetadata = !mod.useMetadata
                3 -> mod.useNbt = !mod.useNbt
                4 -> mod.canDropItems = !mod.canDropItems
                5 -> mod.canGrabItems = !mod.canGrabItems
            }
        }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: EntityPlayer): ItemStack {
        val slot = inventorySlots.getOrNull(slotId) as? SlotFilter
        if (slot == null) return super.slotClick(slotId, dragType, clickTypeIn, player)

        return onFilterSlotClick(slot, dragType, clickTypeIn, player)
    }
}

class ContainerRelay(val tile: TileRelay, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        val inv = tile.inventory
        var i = 0

        iterateArea(0..2, 0..2) { x, y ->
            addSlotToContainer(SlotItemHandler(inv, i++, x * 18 + 62, y * 18 + 13))
        }

        inventoryRegions += InventoryRegion(0..8)
        bindPlayerInventory(player.inventory)
    }
}

class ContainerFilter(val tile: TileFilter, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        val inv = tile.inventory
        var i = 0

        iterateArea(0..2, 0..2) { x, y ->
            addSlotToContainer(SlotFilter(inv, i++, x * 18 + 62, y * 18 + 13))
        }

        inventoryRegions += InventoryRegion(0..8) { _ -> false }
        bindPlayerInventory(player.inventory)
    }

    @Suppress("FoldInitializerAndIfToElvis")
    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: EntityPlayer): ItemStack {
        val slot = inventorySlots.getOrNull(slotId) as? SlotFilter
        if (slot == null) return super.slotClick(slotId, dragType, clickTypeIn, player)

       return onFilterSlotClick(slot, dragType, clickTypeIn, player)
    }
}