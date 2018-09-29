package com.cout970.magneticraft.features.electric_machines

import com.cout970.magneticraft.features.heat_machines.TileElectricHeater
import com.cout970.magneticraft.features.heat_machines.TileRfHeater
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
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

class ContainerElectricFurnace(val tile: TileElectricFurnace, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 93, 17))
            addSlotToContainer(SlotTakeOnly(inv, 1, 93, 49))

            inventoryRegions += InventoryRegion(0..0,
                filter = { FurnaceRecipes.instance().getSmeltingResult(it).isNotEmpty })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }
}

class ContainerThermopile(val tile: TileThermopile, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerWindTurbine(val tile: TileWindTurbine, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerElectricHeater(val tile: TileElectricHeater, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerRfHeater(val tile: TileRfHeater, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}