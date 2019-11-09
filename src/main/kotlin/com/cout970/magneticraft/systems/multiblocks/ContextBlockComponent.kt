package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.IBlockState
import com.cout970.magneticraft.misc.vector.plus
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 28/08/2016.
 */
class ContextBlockComponent(
    val getter: (MultiblockContext) -> IBlockState,
    val stack: ItemStack,
    val replacement: IBlockState,
    val errorMsg: (MultiblockContext, IBlockState, BlockPos) -> ITextComponent
) : IMultiblockComponent {

    override fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent> {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        if (state != getter(context)) {
            // TODO
//            if (Debug.DEBUG && context.player != null && FMLCommonHandler.instance().effectiveSide == Dist.DEDICATED_SERVER) {
//                context.world.setBlockState(pos, getter(context))
//            }
            return listOf(errorMsg(context, state, pos))
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
        context.world.setBlockState(pos, replacement)
        super.activateBlock(relativePos, context)
    }

    override fun deactivateBlock(relativePos: BlockPos, context: MultiblockContext) {
        val pos = context.center + relativePos
        super.deactivateBlock(relativePos, context)
        context.world.setBlockState(pos, getter(context))
    }

    override fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack> = listOf(stack)
}