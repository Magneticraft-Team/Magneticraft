package com.cout970.magneticraft.block

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by Yurgen on 08/11/2016.
 */
abstract class BlockSlabBase(
        material: Material,
        registryName: String,
        unlocalizedName: String = registryName
) : BlockBase(material, registryName, unlocalizedName) {
    protected val AABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)

    override fun canSilkHarvest(): Boolean = false
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = AABB
    override fun isFullyOpaque(state: IBlockState): Boolean = false
    override fun isOpaqueCube(state: IBlockState): Boolean = false
    override fun isFullCube(state: IBlockState): Boolean = false

    override fun doesSideBlockRendering(state: IBlockState, world: IBlockAccess, pos: BlockPos, face: EnumFacing): Boolean {
        return face == EnumFacing.DOWN
    }
}