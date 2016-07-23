package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.TileInfiniteWater
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 23/07/2016.
 */
object BlockInfiniteWater : BlockBase(Material.IRON, "infinite_water"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileInfiniteWater()
}