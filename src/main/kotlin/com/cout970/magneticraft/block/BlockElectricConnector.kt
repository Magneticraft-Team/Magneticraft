package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.aabb.to
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 29/06/2016.
 */
object BlockElectricConnector : BlockBase(Material.IRON, "electric_connector"), ITileEntityProvider {

    val boundingBox by lazy {
        val size = 0.0625 * 4
        Vec3d(0.5 - size, 0.0, 0.5 - size) to Vec3d(0.5 + size, 0.5, 0.5 + size)
    }

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = boundingBox

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileElectricConnector()

    override fun getActualState(state: IBlockState?, worldIn: IBlockAccess?, pos: BlockPos?): IBlockState {
        return super.getActualState(state, worldIn, pos)
    }
}