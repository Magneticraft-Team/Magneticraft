package com.cout970.magneticraft.multiblock.impl

import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.multiblock.BlockData
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import net.minecraft.block.BlockStone
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 20/08/2016.
 */
object MultiblockHydraulicPress : Multiblock() {

    override val name: String = "hydraulic_press"
    override val size: BlockPos = BlockPos(3, 4, 1)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val S: IMultiblockComponent = SingleBlockComponent(Blocks.STONE.defaultState,
                BlockHydraulicPress.defaultState
                        .withProperty(BlockHydraulicPress.PROPERTY_CENTER, false)
                        .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, true))
        val P: IMultiblockComponent = SingleBlockComponent(Blocks.STONE.defaultState.withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH),
                BlockHydraulicPress.defaultState
                        .withProperty(BlockHydraulicPress.PROPERTY_CENTER, false)
                        .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, true))
        val I: IMultiblockComponent = SingleBlockComponent(Blocks.IRON_BLOCK.defaultState,
                BlockHydraulicPress.defaultState
                        .withProperty(BlockHydraulicPress.PROPERTY_CENTER, false)
                        .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, true))
        val F: IMultiblockComponent = SingleBlockComponent(Blocks.OAK_FENCE.defaultState,
                BlockHydraulicPress.defaultState
                        .withProperty(BlockHydraulicPress.PROPERTY_CENTER, false)
                        .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, true))
        val M: IMultiblockComponent = MainBlockComponent(BlockHydraulicPress) { context, state, activate ->
            if (activate) {
                BlockHydraulicPress.defaultState
                        .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, true)
                        .withProperty(BlockHydraulicPress.PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            } else {
                BlockHydraulicPress.defaultState
                        .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, false)
                        .withProperty(BlockHydraulicPress.PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            }
        }

        scheme = yLayers(
                zLayers(listOf(P, P, P)),
                zLayers(listOf(S, F, S)),
                zLayers(listOf(S, I, S)),
                zLayers(listOf(P, M, P))
        )
    }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = listOf()
}