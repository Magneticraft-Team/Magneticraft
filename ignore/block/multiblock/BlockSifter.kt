@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block.multiblock

import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_CENTER
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.impl.MultiblockSifter
import com.cout970.magneticraft.tileentity.multiblock.TileMultiblock
import com.cout970.magneticraft.tileentity.multiblock.TileSifter
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 17/08/2016.
 */
object BlockSifter : BlockMultiblock(Material.IRON, "sifter"), ITileEntityProvider {

    init {
        defaultState = defaultState.withProperty(PROPERTY_CENTER, false).withProperty(PROPERTY_ACTIVE, false)
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB?, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?) {
        if (state[PROPERTY_ACTIVE] && entityBox != null) {
            return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn)
        }
        return addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos))
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        if (state[PROPERTY_ACTIVE]) {
            return super.getSelectedBoundingBox(state, worldIn, pos)
        }
        return FULL_BLOCK_AABB.offset(pos)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean = false
    override fun isFullCube(state: IBlockState): Boolean = false
    override fun isFullBlock(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer?): Boolean {
        return state[PROPERTY_CENTER] && !state[PROPERTY_ACTIVE] && super.canRenderInLayer(state, layer)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? = createTileEntity(worldIn, getStateFromMeta(meta))

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        if (state[PROPERTY_CENTER]) return TileSifter()
        return TileMultiblock()
    }

    override fun removedByPlayer(state: IBlockState?, world: World?, pos: BlockPos?, player: EntityPlayer?, willHarvest: Boolean): Boolean {
        if (state!![PROPERTY_ACTIVE] && world!!.isServer) {
            breakBlock(world, pos!!, state)
            return false
        } else {
            return super.removedByPlayer(state, world, pos, player, willHarvest)
        }
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = 0
        if (state[PROPERTY_CENTER]) {
            meta = meta or 8
        }
        if (state[PROPERTY_ACTIVE]) {
            meta = meta or 4
        }
        val dir = state[PROPERTY_DIRECTION]
        meta = meta or ((dir.ordinal - 2) and 3)
        return meta
    }

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.
            withProperty(PROPERTY_CENTER, (meta and 8) != 0).
            withProperty(PROPERTY_ACTIVE, (meta and 4) != 0).
            withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta and 3))

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, PROPERTY_CENTER, PROPERTY_ACTIVE, PROPERTY_DIRECTION)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        worldIn.setBlockState(pos, defaultState
                .withProperty(PROPERTY_DIRECTION, placer.horizontalFacing.opposite)
                .withProperty(PROPERTY_CENTER, true)
                .withProperty(PROPERTY_ACTIVE, false))
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isServer && hand == EnumHand.MAIN_HAND && state[PROPERTY_CENTER]) {
            if (!state[PROPERTY_ACTIVE]) {
                activateMultiblock(MultiblockContext(MultiblockSifter, worldIn, pos, state[PROPERTY_DIRECTION], playerIn))
            }
        }
        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (state[PROPERTY_ACTIVE]) {
            super.breakBlock(worldIn, pos, state)
        }
    }

    override val stateMapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?
        get() =  {
            block ->
            StateMap.Builder().ignore(PROPERTY_ACTIVE, PROPERTY_CENTER).build().putStateModelLocations(block)
        }
}