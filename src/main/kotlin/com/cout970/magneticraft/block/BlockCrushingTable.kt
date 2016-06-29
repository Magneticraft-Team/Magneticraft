package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import com.cout970.magneticraft.item.hammers.ItemHammer
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

object BlockCrushingTable : BlockBase(
    material = Material.ROCK,
    registryName = "crushing_table"
), ITileEntityProvider {

    val boundingBox = Vec3d.ZERO to Vec3d(1.0, 0.875, 1.0)

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = boundingBox

    override fun createNewTileEntity(worldIn: World, meta: Int) = TileCrushingTable()

    override fun onBlockActivated(
        world: World, pos: BlockPos, state: IBlockState,
        player: EntityPlayer, hand: EnumHand, heldItem: ItemStack?,
        side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (side != EnumFacing.UP) {
            return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)
        }

        val tile = world.getTile<TileCrushingTable>(pos) ?: return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)

        if (tile.getStack() != null) {
            val item = heldItem?.item

            if (tile.canDamage() && item is ItemHammer) {
                tile.doDamage(item.damage)
                item.onHit(heldItem!!, player)
            } else {
                player.inventory.addItemStackToInventory(tile.getStack())
                tile.setStack(null)
            }

            return true
        } else {
            if (heldItem != null) {
                if (CrushingTableRegistry.getRecipe(heldItem) != null) {
                    if (!player.capabilities.isCreativeMode) {
                        heldItem.stackSize--

                        if (heldItem.stackSize == 0) {
                            player.setHeldItem(hand, null)
                        }
                    }

                    tile.setStack(heldItem.copy().apply { stackSize = 1 })

                    return true
                }
            }

            return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)
        }
    }
}