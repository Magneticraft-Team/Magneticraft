@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block


import com.cout970.magneticraft.block.itemblock.ItemBlockFeedingTrough
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.util.vector.isHorizontal
import com.cout970.magneticraft.util.vector.toAABBWith
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 24/06/2016.
 */
object BlockFeedingTrough : BlockModContainer("feeding_trough", Material.WOOD) {

    override fun createItemForm(): ItemBlock? {
        return ItemBlockFeedingTrough(this)
    }

    lateinit var FEEDING_TROUGH_IS_CENTER: PropertyBool
    lateinit var FEEDING_TROUGH_SIDE_POSITION: PropertyDirection
    val boundingBox = Vec3d.ZERO toAABBWith Vec3d(1.0, 0.75, 1.0)

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = boundingBox

    override fun createTileEntity(worldIn: World, meta: IBlockState) =
        if (meta.get(FEEDING_TROUGH_IS_CENTER))
            TileFeedingTrough()
        else null

    override fun createBlockState(): BlockStateContainer {
        FEEDING_TROUGH_IS_CENTER = PropertyBool.create("center")!!
        FEEDING_TROUGH_SIDE_POSITION = PropertyDirection.create("side", { it?.isHorizontal() ?: false })!!
        return BlockStateContainer(this, FEEDING_TROUGH_IS_CENTER, FEEDING_TROUGH_SIDE_POSITION)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer?, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = getTileEntity(worldIn, pos, state)
        if (tile != null) {
            if (heldItem != null) {
                val result = tile.insertItem(heldItem)
                playerIn!!.setHeldItem(hand, result)
                return true
            } else {
                val result = tile.extractItem()
                playerIn!!.setHeldItem(hand, result)
                return true
            }
        }
        return false
    }

    private fun getTileEntity(worldIn: World, pos: BlockPos, state: IBlockState) =
        if (state[FEEDING_TROUGH_IS_CENTER]) {
            worldIn.getTile<TileFeedingTrough>(pos)
        } else {
            val dir = state[FEEDING_TROUGH_SIDE_POSITION]
            worldIn.getTile<TileFeedingTrough>(pos.add(dir.directionVec))
        }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val dir = placer.adjustedHorizontalFacing
        val center = state.withProperty(FEEDING_TROUGH_SIDE_POSITION, dir)?.withProperty(FEEDING_TROUGH_IS_CENTER, true)
        val companion = state.withProperty(FEEDING_TROUGH_SIDE_POSITION, dir.opposite)?.withProperty(FEEDING_TROUGH_IS_CENTER, false)
        worldIn.setBlockState(pos, center)
        worldIn.setBlockState(pos.add(dir.directionVec), companion)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        val dir = state[FEEDING_TROUGH_SIDE_POSITION]
        worldIn.setBlockToAir(pos.add(dir.directionVec))
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        if (!state[FEEDING_TROUGH_IS_CENTER]) {
            return EnumBlockRenderType.INVISIBLE
        }
        return super.getRenderType(state)
    }

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getMetaFromState(state: IBlockState?): Int {
        if (state == null) {
            return 0
        }

        val sideMeta = EnumFacing.HORIZONTALS.indexOf(state[FEEDING_TROUGH_SIDE_POSITION]) shl 1
        val centerMeta = if (state[FEEDING_TROUGH_IS_CENTER]) 1 else 0

        return sideMeta + centerMeta
    }

    override fun getStateFromMeta(meta: Int) = defaultState.run {
        val side = EnumFacing.HORIZONTALS[meta shr 1]
        val center = (meta and 1) == 1
        withProperty(FEEDING_TROUGH_SIDE_POSITION, side).withProperty(FEEDING_TROUGH_IS_CENTER, center)
    }
}