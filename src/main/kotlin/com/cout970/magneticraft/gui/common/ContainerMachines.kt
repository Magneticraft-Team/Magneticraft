package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/06/12.
 */

class ContainerBox(player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileBox>(blockPos)!!

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

class ContainerBattery(player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileBattery>(blockPos)!!

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerElectricFurnace(player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileElectricFurnace>(blockPos)!!

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