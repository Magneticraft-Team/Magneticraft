package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.TileCrushingTable
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockCrushingTable : BlockBase(
    material = Material.ROCK,
    registryName = "crushing_table"
), ITileEntityProvider {
    override fun createNewTileEntity(worldIn: World, meta: Int) = TileCrushingTable()

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float) =
        super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)

}