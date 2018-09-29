package com.cout970.magneticraft.features.heat_machines

import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/08/10.
 */

class ContainerCombustionChamber(val tile: TileCombustionChamber, player: EntityPlayer, world: World,
                                 blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 91, 32))
            inventoryRegions += InventoryRegion(0..0, filter = { it.isNotEmpty && it.item == Items.COAL })
        }
        bindPlayerInventory(player.inventory)
    }
}


class ContainerSteamBoiler(val tile: TileSteamBoiler, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerGasificationUnit(val tile: TileGasificationUnit, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.inv.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 83, 17))
            addSlotToContainer(SlotTakeOnly(inv, 1, 83, 49))

            inventoryRegions += InventoryRegion(0..0,
                filter = { GasificationUnitRecipeManager.findRecipe(it) != null })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }
}