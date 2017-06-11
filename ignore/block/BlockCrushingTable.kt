package com.cout970.magneticraft.block

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager
import com.cout970.magneticraft.item.hammers.ItemHammer
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.util.vector.toAABBWith
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

object BlockCrushingTable : BlockModContainer("crushing_table", Material.ROCK) {

    val boundingBox = Vec3d.ZERO toAABBWith  Vec3d(1.0, 0.875, 1.0)

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = boundingBox

    override fun createTileEntity(world: World, state: IBlockState) = TileCrushingTable()

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
                if (tile.getStack()!!.isItemEqual(ItemStack(Items.BLAZE_ROD))) {
                    player.setFire(5)
                }
                item.onHit(heldItem!!, player)
            } else {
                player.inventory.addItemStackToInventory(tile.getStack())
                tile.setStack(null)
                tile.sendUpdateToNearPlayers()
            }

            return true
        } else {
            if (heldItem != null) {
                if (heldItem.item is ItemHammer) {
                    for (slot in 0 until player.inventory.sizeInventory) {
                        val stack = player.inventory.getStackInSlot(slot)
                        if (stack != null && CrushingTableRecipeManager.findRecipe(stack) != null) {
                            if (!player.capabilities.isCreativeMode) {
                                stack.stackSize--

                                if (stack.stackSize <= 0) {
                                    player.inventory.setInventorySlotContents(slot, null)
                                }
                            }

                            tile.setStack(stack.copy().apply { stackSize = 1 })
                            tile.sendUpdateToNearPlayers()
                            return true
                        }
                    }
                } else {
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
            }
            return false
        }
    }
}