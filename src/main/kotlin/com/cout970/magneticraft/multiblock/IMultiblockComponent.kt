package com.cout970.magneticraft.multiblock

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 */
interface IMultiblockComponent {
    fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent>
    fun getBlockData(relativePos: BlockPos, context: MultiblockContext): BlockData
    fun activateBlock(relativePos: BlockPos, context: MultiblockContext)
    fun deactivateBlock(relativePos: BlockPos, context: MultiblockContext)
    fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack>
}