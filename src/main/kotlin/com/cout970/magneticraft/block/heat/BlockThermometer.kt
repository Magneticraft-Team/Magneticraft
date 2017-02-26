package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
// TODO add to game or remove
@Suppress("unused")
object BlockThermometer : BlockMultiState(Material.ROCK, "thermometer_block") {

    override fun onBlockPlaced(worldIn: World?, pos: BlockPos?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        worldIn?.setBlockState(pos, defaultState.withProperty(PROPERTY_DIRECTION, facing))
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        worldIn?.scheduleUpdate(pos, this, 1)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        worldIn.updateComparatorOutputLevel(pos, this)
        super.breakBlock(worldIn, pos, state)
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getWeakPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        val tile = blockAccess.getTileEntity(pos.offset(blockState[PROPERTY_DIRECTION])) ?: return 0
        val handler = HEAT_NODE_HANDLER!!.fromTile(tile) ?: return 0
        if (handler is TraitHeat)
            return handler.getComparatorOutput()
        return 0
    }

    override fun getStrongPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        val tile = blockAccess.getTileEntity(pos.offset(blockState[PROPERTY_DIRECTION])) ?: return 0
        val handler = HEAT_NODE_HANDLER!!.fromTile(tile) ?: return 0
        if (handler is TraitHeat)
            return handler.getComparatorOutput()
        return 0
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        val tile = worldIn.getTileEntity(pos.offset(blockState[PROPERTY_DIRECTION])) ?: return 0
        val handler = HEAT_NODE_HANDLER!!.fromTile(tile) ?: return 0
        if (handler is TraitHeat)
            return handler.getComparatorOutput()
        return 0
    }

    override fun getMetaFromState(state: IBlockState): Int = state[PROPERTY_DIRECTION].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(PROPERTY_DIRECTION, EnumFacing.getFront(meta))

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_DIRECTION)
}