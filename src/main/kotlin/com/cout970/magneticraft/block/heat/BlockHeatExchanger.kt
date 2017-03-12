package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.tileentity.heat.TileHeatExchanger
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
//TODO add to game or remove
@Suppress("unused")
object BlockHeatExchanger : BlockHeatBase(Material.ROCK, "heat_exchanger") {

    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileHeatExchanger()

}