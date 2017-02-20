package com.cout970.magneticraft.multiblock.components

import com.cout970.magneticraft.multiblock.*
import com.cout970.magneticraft.util.i18n
import com.cout970.magneticraft.util.vector.plus
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 20/08/2016.
 */
class MainBlockComponent(val block: Block, val getter: (context: MultiblockContext, state: IBlockState, activate: Boolean) -> IBlockState) : IMultiblockComponent {

    override fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent> {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        if (state.block != block) {
            val keyStr = "text.magneticraft.multiblock.invalid_block"
            val vecStr = "[%d, %d, %d]".format(pos.x, pos.y, pos.z)
            return listOf(keyStr.i18n(vecStr, state.block.localizedName, block.localizedName))
        }
        return emptyList()
    }

    override fun getBlockData(relativePos: BlockPos, context: MultiblockContext): BlockData {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        return BlockData(state, pos)
    }

    override fun activateBlock(relativePos: BlockPos, context: MultiblockContext) {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        context.world.setBlockState(pos, getter(context, state, true))
        val tile = context.world.getTileEntity(pos)
        if (tile is ITileMultiblock) {
            tile.multiblock = context.multiblock
            tile.multiblockFacing = context.facing
            tile.centerPos = relativePos
            tile.onActivate()
        }
    }

    override fun deactivateBlock(relativePos: BlockPos, context: MultiblockContext) {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        val tile = context.world.getTileEntity(pos)
        if (tile is ITileMultiblock) {
            tile.onDeactivate()
            tile.multiblock = null
            tile.multiblockFacing = null
            tile.centerPos = null
        }
        context.world.setBlockState(pos, getter(context, state, false))
    }

    override fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack> = listOf()
}