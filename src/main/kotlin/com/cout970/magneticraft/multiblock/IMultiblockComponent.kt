package com.cout970.magneticraft.multiblock

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 */
interface IMultiblockComponent {

    //if the block is not correct, return a list of errors to show to the player
    fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent>

    //returns the current ste of the block, used for extra requirements in the multiblock
    fun getBlockData(relativePos: BlockPos, context: MultiblockContext): BlockData

    //called when the multiblock is activated
    fun activateBlock(relativePos: BlockPos, context: MultiblockContext)

    //called when the multiblock is destroyed
    fun deactivateBlock(relativePos: BlockPos, context: MultiblockContext)

    //return the items to render in the blueprint of this block
    fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack>
}