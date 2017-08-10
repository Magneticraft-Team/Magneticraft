package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.tileentity.TileCombustionChamber
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