package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.aabb.to
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.block.itemblock.ItemBlockFeedingTrough
import com.cout970.magneticraft.block.states.BlockProperties
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
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
object BlockFeedingTrough : BlockMultiState(Material.WOOD, "feeding_trough"), ITileEntityProvider {

    val STATE_MAP = mapOf(
            0 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, true).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.NORTH),
            1 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, true).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.SOUTH),
            2 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, true).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.WEST),
            3 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, true).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.EAST),

            4 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, false).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.NORTH),
            5 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, false).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.SOUTH),
            6 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, false).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.WEST),
            7 to defaultState.withProperty(BlockProperties.blockFeedingTroughCenter, false).withProperty(BlockProperties.blockFeedingTroughCompanion, EnumFacing.EAST)
    )

    val BOUNDING_BOX = Vec3d.ZERO to Vec3d(1.0, 0.75, 1.0)

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = BOUNDING_BOX

    override fun getModels() = mapOf(0 to ModelResourceLocation(registryName, "inventory"))

    override fun isHiddenState(state: IBlockState, meta: Int): Boolean = meta != 0

    override fun getProperties(): Array<IProperty<*>> = arrayOf(BlockProperties.blockFeedingTroughCenter, BlockProperties.blockFeedingTroughCompanion)

    override fun getStateMap(): Map<Int, IBlockState> = STATE_MAP

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if (getStateFromMeta(meta)?.getValue(BlockProperties.blockFeedingTroughCenter) ?: false) return TileFeedingTrough()
        return null
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer?, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = getTileEntity(worldIn, pos, state)
        if (tile != null) {
            if (heldItem != null) {
                val result = tile.insetItem(heldItem)
                playerIn!!.setHeldItem(hand, result)
                return true
            }else{
                val result = tile.extractItem()
                playerIn!!.setHeldItem(hand, result)
                return true
            }
        }
        return false
    }

    private fun getTileEntity(worldIn: World, pos: BlockPos, state: IBlockState): TileFeedingTrough? {
        if (state.getValue(BlockProperties.blockFeedingTroughCenter)) {
            val tile = worldIn.getTileEntity(pos)
            if (tile is TileFeedingTrough) return tile
        } else {
            val dir = state.getValue(BlockProperties.blockFeedingTroughCompanion)
            val tile = worldIn.getTileEntity(pos.add(dir.directionVec))
            if (tile is TileFeedingTrough) return tile
        }
        return null
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val dir = placer.adjustedHorizontalFacing
        val center = state.withProperty(BlockProperties.blockFeedingTroughCompanion, dir)?.withProperty(BlockProperties.blockFeedingTroughCenter, true)
        val companion = state.withProperty(BlockProperties.blockFeedingTroughCompanion, dir.opposite)?.withProperty(BlockProperties.blockFeedingTroughCenter, false)
        worldIn.setBlockState(pos, center)
        worldIn.setBlockState(pos.add(dir.directionVec), companion)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        val dir = state.getValue(BlockProperties.blockFeedingTroughCompanion)
        worldIn.setBlockToAir(pos.add(dir.directionVec))
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        if (!state.getValue(BlockProperties.blockFeedingTroughCenter)) {
            return EnumBlockRenderType.INVISIBLE
        }
        return super.getRenderType(state)
    }

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun createItemBlock() = ItemBlockFeedingTrough()
}