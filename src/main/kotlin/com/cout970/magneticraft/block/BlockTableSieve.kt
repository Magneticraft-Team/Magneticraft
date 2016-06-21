package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.TileTableSieve
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 13/06/2016.
 */
object BlockTableSieve : BlockBase(Material.WOOD, "table_sieve"), ITileEntityProvider{

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? = TileTableSieve()
}