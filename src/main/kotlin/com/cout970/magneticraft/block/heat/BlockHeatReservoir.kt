package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.tileentity.heat.TileHeatReservoir
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockHeatReservoir : BlockHeatBase(Material.ROCK, "heat_reservoir"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileHeatReservoir()

}