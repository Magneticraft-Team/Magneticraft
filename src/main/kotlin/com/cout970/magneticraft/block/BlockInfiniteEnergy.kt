package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.electric.TileInfiniteEnergy
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 27/07/2016.
 */
object BlockInfiniteEnergy : BlockBase(Material.IRON, "infinite_energy"), ITileEntityProvider{

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileInfiniteEnergy()
}