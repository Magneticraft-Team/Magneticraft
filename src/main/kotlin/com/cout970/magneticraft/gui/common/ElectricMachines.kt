package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/08/10.
 */

class ContainerBattery(val tile: TileBattery, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 102, 16))
            addSlotToContainer(SlotItemHandler(inv, 1, 102, 48))

            inventoryRegions += InventoryRegion(0..0, filter = { FORGE_ENERGY!!.fromItem(it)?.canReceive() ?: false })
            inventoryRegions += InventoryRegion(1..1, filter = { FORGE_ENERGY!!.fromItem(it)?.canExtract() ?: false })
        }
        bindPlayerInventory(player.inventory)
    }
}

class ContainerElectricFurnace(tile: TileElectricFurnace, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 91, 16))
            addSlotToContainer(SlotTakeOnly(inv, 1, 91, 48))

            inventoryRegions += InventoryRegion(0..0,
                    filter = { FurnaceRecipes.instance().getSmeltingResult(it).isNotEmpty })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }
}