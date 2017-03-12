package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.tileentity.heat.TileHeatReservoir
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockHeatReservoir : BlockHeatBase(Material.ROCK, "heat_reservoir") {

    init {
        tickRandomly = true
    }
    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileHeatReservoir()

}