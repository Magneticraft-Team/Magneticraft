package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.misc.vector.plus
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 2017/07/17.
 */

object IgnoreBlockComponent : IMultiblockComponent {

    override fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent> = emptyList()

    override fun getBlockData(relativePos: BlockPos, context: MultiblockContext): BlockData {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        return BlockData(state, pos)
    }

    override fun activateBlock(relativePos: BlockPos, context: MultiblockContext) = Unit

    override fun deactivateBlock(relativePos: BlockPos, context: MultiblockContext) = Unit

    override fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack> = listOf()
}