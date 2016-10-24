package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.tileentity.electric.TileInfiniteHeat
import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockInfiniteHeat : BlockHeatBase(Material.ROCK, "infinite_heat"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileInfiniteHeat(1800.toKelvinFromCelsius())
}