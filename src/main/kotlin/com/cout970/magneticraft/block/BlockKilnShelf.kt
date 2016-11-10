package com.cout970.magneticraft.block

/**
 * Created by Yurgen on 09/11/2016.
 */

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.tileentity.TileKilnShelf
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockKilnShelf : BlockBase(Material.IRON, "kiln_shelf"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileKilnShelf()

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