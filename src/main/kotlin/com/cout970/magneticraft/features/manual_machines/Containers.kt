package com.cout970.magneticraft.features.manual_machines

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.gui.SlotFabricator
import com.cout970.magneticraft.misc.gui.SlotLegacyFilter
import com.cout970.magneticraft.misc.inventory.*
import com.cout970.magneticraft.misc.iterateArea
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/08/10.
 */

class ContainerBox(val tile: TileBox, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            for (index in 0 until inv.slots) {
                val pos = getPosFromIndex(index)
                addSlotToContainer(SlotItemHandler(inv, index, pos.xi, pos.yi))
            }
            inventoryRegions += InventoryRegion(0..26)
        }
        bindPlayerInventory(player.inventory)
    }

    private fun getPosFromIndex(index: Int): IVector2 {
        return vec2Of(index % 9, index / 9) * 18 + vec2Of(8, 8)
    }
}

class ContainerFabricator(val tile: TileFabricator, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        var i = 0
        // Item in the internal inventory
        iterateArea(0..2, 0..2) { x, y ->
            addSlotToContainer(SlotItemHandler(tile.inventory, i++, x * 18 + 107, y * 18 + 14))
        }
        inventoryRegions += InventoryRegion(0..8)

        i = 0
        // Items in the crafting grid
        iterateArea(0..2, 0..2) { x, y ->
            addSlotToContainer(SlotLegacyFilter(tile.fabricatorModule.recipeGrid, i++, x * 18 + 17, y * 18 + 14))
        }
        inventoryRegions += InventoryRegion(9..17, filter = { false })

        // Output of the crafting recipe
        addSlotToContainer(SlotFabricator(tile.fabricatorModule.craftingResult, 0, 80, 32))
        inventoryRegions += InventoryRegion(18..18, filter = { false })

        bindPlayerInventory(player.inventory)
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getByteArray(1) { array ->
            val input = CompressedStreamTools.readCompressed(array.inputStream())
            val inv = Inventory(9)
            inv.deserializeNBT(input)

            val grid = tile.fabricatorModule.recipeGrid
            repeat(9) { slot ->
                grid.setInventorySlotContents(slot, inv[slot])
            }
        }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: EntityPlayer): ItemStack {
        if (inventorySlots.getOrNull(slotId) is SlotFabricator && player.world.isServer) {
            if (dragType == 1) {
                repeat(9) {
                    tile.fabricatorModule.recipeGrid.setInventorySlotContents(it, ItemStack.EMPTY)
                }
//            tile.fabricatorModule.clearGrid()
            } else if (dragType == 0) {
                tile.fabricatorModule.requestItemCraft()
            }
        }
        val slot = inventorySlots.getOrNull(slotId) as? SlotLegacyFilter
        if (slot == null) return super.slotClick(slotId, dragType, clickTypeIn, player)

        var result = ItemStack.EMPTY
        val playerInv = player.inventory

        if (clickTypeIn != ClickType.PICKUP && clickTypeIn != ClickType.PICKUP_ALL) {
            return result
        }
        if (dragType != 0 && dragType != 1) return result

        if (slotId == -999) {
            if (!playerInv.itemStack.isEmpty) {
                if (dragType == 0) {
                    player.dropItem(playerInv.itemStack, true)
                    playerInv.itemStack = ItemStack.EMPTY
                }

                if (dragType == 1) {
                    player.dropItem(playerInv.itemStack.splitStack(1), true)
                }
            }
            return result
        }

        val slotStack = slot.stack
        val handStack = playerInv.itemStack

        if (!slotStack.isEmpty) {
            result = slotStack.copy()
        }

        if (handStack.isEmpty) {
            // Empty hand, clear the slot item
            slot.putStack(ItemStack.EMPTY)
        } else {
            // Replace slot contents with the player's hand item
            if (handStack.isNotEmpty) {
                slot.putStack(handStack.withSize(1))
            } else {
                slot.putStack(ItemStack.EMPTY)
            }
        }

        slot.onSlotChanged()

        return result
    }
}