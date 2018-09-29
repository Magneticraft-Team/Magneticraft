package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.misc.i18n
import com.cout970.magneticraft.misc.prettyFormat
import com.cout970.magneticraft.misc.vector.plus
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by cout970 on 20/08/2016.
 */

class SingleBlockComponent(val origin: IBlockState, val replacement: IBlockState) : IMultiblockComponent {

    override fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent> {
        val pos = context.center + relativePos
        val state = context.world.getBlockState(pos)
        if (state != origin) {
            if (Debug.DEBUG && context.player != null && FMLCommonHandler.instance().effectiveSide == Side.SERVER) {
                context.world.setBlockState(pos, origin)
            }
            val keyStr = "text.magneticraft.multiblock.invalid_block"
            val vecStr = "[%d, %d, %d]".format(pos.x, pos.y, pos.z)
            return listOf(keyStr.i18n(vecStr, state.prettyFormat(), origin.prettyFormat()))
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
        super.deactivateBlock(relativePos, context)
        val pos = context.center + relativePos
        if (origin != Blocks.AIR.defaultState) //Special case, don't overwrite blocks with air
            context.world.setBlockState(pos, origin)
    }

    override fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack> = listOf(
        ItemStack(origin.block, 1, origin.block.getMetaFromState(origin)))
}