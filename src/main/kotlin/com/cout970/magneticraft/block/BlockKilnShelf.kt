package com.cout970.magneticraft.block

/**
 * Created by Yurgen on 09/11/2016.
 */


import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileKilnShelf
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

object BlockKilnShelf : BlockModContainer("kiln_shelf", Material.IRON) {

    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileKilnShelf()

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = BlockTableSieve.TABLE_SIEVE_BOX

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun onBlockActivated(
            world: World, pos: BlockPos, state: IBlockState,
            player: EntityPlayer, hand: EnumHand, heldItem: ItemStack?,
            side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (side != EnumFacing.UP) {
            return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)
        }

        val tile = world.getTile<TileKilnShelf>(pos) ?: return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)

        if (tile.getStack() != null) {
            player.inventory.addItemStackToInventory(tile.getStack())
            tile.setStack(null)
            tile.sendUpdateToNearPlayers()
            return true
        } else {
            if (heldItem != null) {
                if (!player.capabilities.isCreativeMode) {
                    heldItem.stackSize--

                    if (heldItem.stackSize <= 0) {
                        player.setHeldItem(hand, null)
                    }
                }

                tile.setStack(heldItem.copy().apply { stackSize = 1 })
                tile.sendUpdateToNearPlayers()
                return true
            }
            return false
        }
    }
}