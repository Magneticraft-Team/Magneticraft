package com.cout970.magneticraft.features.manual_machines

import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.gui.AutoContainer
import com.cout970.magneticraft.systems.gui.GuiBuilder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */

class ContainerFabricator(builder: GuiBuilder, configFunc: (AutoContainer) -> Unit, player: EntityPlayer, world: World, blockPos: BlockPos)
    : AutoContainer(builder, configFunc, player, world, blockPos) {

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getByteArray(1) { array ->
            val input = CompressedStreamTools.readCompressed(array.inputStream())
            val inv = Inventory(9)
            inv.deserializeNBT(input)

            val grid = (tileEntity as TileFabricator).fabricatorModule.recipeGrid
            repeat(9) { slot ->
                grid.setInventorySlotContents(slot, inv[slot])
            }
        }
    }
}