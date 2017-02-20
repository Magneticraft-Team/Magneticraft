package com.cout970.magneticraft.block

import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.TileInserter
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 25/08/2016.
 */
// TODO add to game or remove
@Suppress("unused")
object BlockInserter : BlockMultiState(Material.IRON, "inserter"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileInserter()

    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.INVISIBLE

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getMetaFromState(state: IBlockState): Int = state[PROPERTY_DIRECTION].horizontalIndex

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta))

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_DIRECTION)

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        worldIn.setBlockState(pos, defaultState.withProperty(PROPERTY_DIRECTION, placer.horizontalFacing.opposite))
    }
}