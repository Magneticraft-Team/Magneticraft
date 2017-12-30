package com.cout970.magneticraft.multiblock.core

import com.cout970.magneticraft.block.MultiblockParts
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import net.minecraft.block.Block

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

fun Multiblock.collumnBlock(): SingleBlockComponent {
    return SingleBlockComponent(MultiblockParts.column.defaultState, Multiblocks.gap.defaultState)
}

fun Multiblock.mainBlockOf(it: Block): MainBlockComponent {
    return MainBlockComponent(it) { context, activate ->
        Multiblocks.MultiblockOrientation.of(context.facing, activate).getBlockState(it)
    }
}