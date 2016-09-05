package com.cout970.magneticraft.multiblock.impl

import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.block.decoration.BlockMachineBlock
import com.cout970.magneticraft.block.decoration.BlockMachineBlockSupportColumn
import com.cout970.magneticraft.block.decoration.BlockStripedMachineBlock
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.multiblock.BlockData
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.components.ContextBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
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
        val replacement = BlockHydraulicPress.defaultState
                .withProperty(BlockHydraulicPress.PROPERTY_CENTER, false)
                .withProperty(BlockHydraulicPress.PROPERTY_ACTIVE, true)

        val S: IMultiblockComponent = SingleBlockComponent(
                BlockMachineBlockSupportColumn.defaultState.withProperty(BlockMachineBlockSupportColumn.PROPERTY_STATES,
                        BlockMachineBlockSupportColumn.States.LINES_Y), replacement)

        val B: IMultiblockComponent = SingleBlockComponent(BlockMachineBlock.defaultState, replacement)

        val P: IMultiblockComponent = ContextBlockComponent(
                { ctx ->
                    BlockMachineBlockSupportColumn.defaultState.withProperty(BlockMachineBlockSupportColumn.PROPERTY_STATES,
                            BlockMachineBlockSupportColumn.States.fromAxis(ctx.facing.rotateY().axis))
                }, ItemStack(BlockMachineBlockSupportColumn, 1, 1), replacement)

        val I: IMultiblockComponent = SingleBlockComponent(Blocks.IRON_BLOCK.defaultState, replacement)
        val F: IMultiblockComponent = SingleBlockComponent(BlockStripedMachineBlock.defaultState, replacement)

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
                zLayers(listOf(B, M, B))
        )
    }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = listOf()
}