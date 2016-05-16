package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.aabb.to
import com.cout970.magneticraft.tileentity.TileCrushingTable
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

val CRUSHING_TABLE_BOX = Vec3d.ZERO to Vec3d(1.0, 0.875, 1.0)

object BlockCrushingTable : BlockBase(
    material = Material.ROCK,
    registryName = "crushing_table"
), ITileEntityProvider {
    init {

    }

    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = CRUSHING_TABLE_BOX

    override fun createNewTileEntity(worldIn: World, meta: Int) = TileCrushingTable()

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float) =
        super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)
}