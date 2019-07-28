package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.features.multiblock_parts.Blocks.ColumnOrientation.*
import com.cout970.magneticraft.features.multiblock_parts.toColumnAxis
import com.cout970.magneticraft.misc.i18n
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.prettyFormat
import com.cout970.magneticraft.misc.vector.getRelative
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import com.cout970.magneticraft.features.multiblock_parts.Blocks as MultiblockParts
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks


fun Multiblock.ofBlock(block: Block): SingleBlockComponent {
    return SingleBlockComponent(block.defaultState, Multiblocks.gap.defaultState)
}

fun Multiblock.airBlock(): SingleBlockComponent {
    return SingleBlockComponent(Blocks.AIR.defaultState, Blocks.AIR.defaultState)
}

fun Multiblock.copperCoilBlock(): SingleBlockComponent {
    val block = MultiblockParts.PartType.COPPER_COIL.getBlockState(MultiblockParts.parts)
    return SingleBlockComponent(block, Multiblocks.gap.defaultState)
}

fun Multiblock.grateBlock(): SingleBlockComponent {
    val block = MultiblockParts.PartType.GRATE.getBlockState(MultiblockParts.parts)
    return SingleBlockComponent(block, Multiblocks.gap.defaultState)
}

fun Multiblock.baseBlock(): SingleBlockComponent {
    val block = MultiblockParts.PartType.BASE.getBlockState(MultiblockParts.parts)
    return SingleBlockComponent(block, Multiblocks.gap.defaultState)
}

fun Multiblock.corrugatedIronBlock(): SingleBlockComponent {
    val block = MultiblockParts.PartType.CORRUGATED_IRON.getBlockState(MultiblockParts.parts)
    return SingleBlockComponent(block, Multiblocks.gap.defaultState)
}

fun Multiblock.strippedBlock(): SingleBlockComponent {
    val block = MultiblockParts.PartType.STRIPED.getBlockState(MultiblockParts.parts)
    return SingleBlockComponent(block, Multiblocks.gap.defaultState)
}

fun Multiblock.electricBlock(): SingleBlockComponent {
    val block = MultiblockParts.PartType.ELECTRIC.getBlockState(MultiblockParts.parts)
    return SingleBlockComponent(block, Multiblocks.gap.defaultState)
}

fun Multiblock.columnBlock(dir: EnumFacing): ContextBlockComponent {
    val block = MultiblockParts.column
    return ContextBlockComponent(
        getter = { ctx -> ctx.facing.getRelative(dir).axis.toColumnAxis().getBlockState(block) },
        stack = block.defaultState.stack(),
        replacement = Multiblocks.gap.defaultState,
        errorMsg = { ctx, state, pos ->
            val vecStr = "[%d, %d, %d]".format(pos.x, pos.y, pos.z)
            val axis = ctx.facing.getRelative(dir).axis.toColumnAxis()
            val expected = axis.getBlockState(block)

            if (state.block == expected.block) {
                val keyStr = "text.magneticraft.multiblock.invalid_column_orientation"
                val axisName = when (axis) {
                    AXIS_Y -> "Axis Y"
                    AXIS_X -> "Axis X"
                    AXIS_Z -> "Axis Z"
                }
                keyStr.i18n(vecStr, state.prettyFormat(), axisName)
            } else {
                val keyStr = "text.magneticraft.multiblock.invalid_block"
                keyStr.i18n(vecStr, state.prettyFormat(), expected.prettyFormat())
            }
        }
    )
}

fun Multiblock.mainBlockOf(it: Block): MainBlockComponent {
    return MainBlockComponent(it) { context, activate ->
        Multiblocks.MultiblockOrientation.of(context.facing, activate).getBlockState(it)
    }
}