package com.cout970.magneticraft.multiblock.core

import com.cout970.magneticraft.block.MultiblockParts
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.block.toColumnAxis
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.multiblock.components.ContextBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.util.vector.getRelative
import net.minecraft.block.Block
import net.minecraft.util.EnumFacing

fun Multiblock.ofBlock(block: Block): SingleBlockComponent {
    return SingleBlockComponent(block.defaultState, Multiblocks.gap.defaultState)
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
            { ctx -> ctx.facing.getRelative(dir).axis.toColumnAxis().getBlockState(block) },
            block.defaultState.stack(), Multiblocks.gap.defaultState
    )
}

fun Multiblock.mainBlockOf(it: Block): MainBlockComponent {
    return MainBlockComponent(it) { context, activate ->
        Multiblocks.MultiblockOrientation.of(context.facing, activate).getBlockState(it)
    }
}