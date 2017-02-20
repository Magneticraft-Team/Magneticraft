package com.cout970.magneticraft.multiblock.impl

import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_CENTER
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.block.decoration.BlockMachineBlock
import com.cout970.magneticraft.block.decoration.BlockMachineBlockSupportColumn
import com.cout970.magneticraft.block.decoration.BlockStripedMachineBlock
import com.cout970.magneticraft.block.multiblock.BlockGrinder
import com.cout970.magneticraft.multiblock.BlockData
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.components.ContextBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.tilerenderer.PIXEL
import com.cout970.magneticraft.util.vector.times
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 20/08/2016.
 */
object MultiblockGrinder : Multiblock() {

    override val name: String = "grinder"
    override val size: BlockPos = BlockPos(3, 4, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val replacement = BlockGrinder.defaultState
                .withProperty(PROPERTY_CENTER, false)
                .withProperty(PROPERTY_ACTIVE, true)

        val S: IMultiblockComponent = SingleBlockComponent(
                BlockMachineBlockSupportColumn.defaultState.withProperty(BlockMachineBlockSupportColumn.PROPERTY_STATES,
                        BlockMachineBlockSupportColumn.States.LINES_Y), replacement)

        val B: IMultiblockComponent = SingleBlockComponent(BlockMachineBlock.defaultState, replacement)

        val P: IMultiblockComponent = ContextBlockComponent({ ctx ->
            BlockMachineBlockSupportColumn.defaultState.withProperty(
                    BlockMachineBlockSupportColumn.PROPERTY_STATES,
                    BlockMachineBlockSupportColumn.States.fromAxis(ctx.facing.rotateY().axis))
        }, ItemStack(BlockMachineBlockSupportColumn, 1, 1), replacement)

        val O: IMultiblockComponent = ContextBlockComponent({ ctx ->
            BlockMachineBlockSupportColumn.defaultState.withProperty(
                    BlockMachineBlockSupportColumn.PROPERTY_STATES,
                    BlockMachineBlockSupportColumn.States.fromAxis(ctx.facing.axis))
        }, ItemStack(BlockMachineBlockSupportColumn, 1, 2), replacement)

        val A: IMultiblockComponent = SingleBlockComponent(Blocks.AIR.defaultState, Blocks.AIR.defaultState)

        val F: IMultiblockComponent = SingleBlockComponent(BlockStripedMachineBlock.defaultState, replacement)

        val M: IMultiblockComponent = MainBlockComponent(BlockGrinder) { context, state, activate ->
            if (activate) {
                BlockGrinder.defaultState
                        .withProperty(PROPERTY_ACTIVE, true)
                        .withProperty(PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            } else {
                BlockGrinder.defaultState
                        .withProperty(PROPERTY_ACTIVE, false)
                        .withProperty(PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            }
        }

        scheme = yLayers(
                zLayers(listOf(F, F, F),
                        listOf(F, B, F),
                        listOf(F, F, F)),
                zLayers(listOf(S, P, S),
                        listOf(O, B, O),
                        listOf(S, P, S)),
                zLayers(listOf(S, B, S),
                        listOf(A, B, A),
                        listOf(S, B, S)),
                zLayers(listOf(B, M, B),
                        listOf(B, B, B),
                        listOf(B, B, B)))
    }

    override fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf(
            Vec3d(24.954, 14.000, 40.946) * PIXEL to Vec3d(30.954, 58.000, 46.946) * PIXEL,
            Vec3d(-15.046, 14.000, 40.946) * PIXEL to Vec3d(-9.046, 58.000, 46.946) * PIXEL,
            Vec3d(-15.046, 14.000, 0.946) * PIXEL to Vec3d(-9.046, 58.000, 6.946) * PIXEL,
            Vec3d(-10.046, 40.624, 32.956) * PIXEL to Vec3d(25.954, 64.000, 44.728) * PIXEL,
            Vec3d(-16.046, 58.000, 41.946) * PIXEL to Vec3d(31.954, 64.000, 47.946) * PIXEL,
            Vec3d(-12.828, 40.624, 5.946) * PIXEL to Vec3d(-1.056, 64.000, 41.946) * PIXEL,
            Vec3d(-16.046, 58.000, 5.946) * PIXEL to Vec3d(-10.046, 64.000, 41.946) * PIXEL,
            Vec3d(24.954, 14.000, 0.946) * PIXEL to Vec3d(30.954, 58.000, 6.946) * PIXEL,
            Vec3d(-10.046, 40.624, 3.165) * PIXEL to Vec3d(25.954, 64.000, 14.937) * PIXEL,
            Vec3d(-16.046, 58.000, -0.054) * PIXEL to Vec3d(31.954, 64.000, 5.946) * PIXEL,
            Vec3d(25.954, 58.000, 5.946) * PIXEL to Vec3d(31.954, 64.000, 41.946) * PIXEL,
            Vec3d(16.963, 40.624, 5.946) * PIXEL to Vec3d(28.735, 64.000, 41.946) * PIXEL,
            Vec3d(1.954, 11.000, 17.946) * PIXEL to Vec3d(13.954, 55.000, 29.946) * PIXEL,
            Vec3d(-12.046, 0.000, 3.946) * PIXEL to Vec3d(27.954, 12.000, 43.946) * PIXEL,
            Vec3d(-4.046, 12.000, 33.946) * PIXEL to Vec3d(19.954, 42.000, 35.946) * PIXEL,
            Vec3d(17.954, 12.000, 13.946) * PIXEL to Vec3d(19.954, 42.000, 33.946) * PIXEL,
            Vec3d(-4.046, 12.000, 11.946) * PIXEL to Vec3d(19.954, 42.000, 13.946) * PIXEL,
            Vec3d(23.954, 0.000, -0.054) * PIXEL to Vec3d(31.954, 14.000, 7.946) * PIXEL,
            Vec3d(19.954, 40.000, 11.946) * PIXEL to Vec3d(27.954, 42.000, 35.946) * PIXEL,
            Vec3d(22.954, 42.000, 3.946) * PIXEL to Vec3d(27.954, 56.000, 8.946) * PIXEL,
            Vec3d(-12.046, 40.000, 3.946) * PIXEL to Vec3d(27.954, 42.000, 11.946) * PIXEL,
            Vec3d(-12.046, 40.000, 11.946) * PIXEL to Vec3d(-4.046, 42.000, 35.946) * PIXEL,
            Vec3d(-12.046, 40.000, 35.946) * PIXEL to Vec3d(27.954, 42.000, 43.946) * PIXEL,
            Vec3d(-16.046, 0.000, -0.054) * PIXEL to Vec3d(-8.046, 14.000, 7.946) * PIXEL,
            Vec3d(-4.046, 12.000, 13.946) * PIXEL to Vec3d(-2.046, 42.000, 33.946) * PIXEL,
            Vec3d(23.954, 0.000, 39.946) * PIXEL to Vec3d(31.954, 14.000, 47.946) * PIXEL,
            Vec3d(-16.046, 0.000, 39.946) * PIXEL to Vec3d(-8.046, 14.000, 47.946) * PIXEL,
            Vec3d(-12.046, 42.000, 3.946) * PIXEL to Vec3d(-7.046, 56.000, 8.946) * PIXEL,
            Vec3d(-12.046, 42.000, 38.946) * PIXEL to Vec3d(-7.046, 56.000, 43.946) * PIXEL,
            Vec3d(22.954, 42.000, 38.946) * PIXEL to Vec3d(27.954, 56.000, 43.946) * PIXEL,
            Vec3d(-0.046, 12.000, 35.946) * PIXEL to Vec3d(15.954, 20.000, 43.946) * PIXEL,
            Vec3d(0.954, 11.460, 43.540) * PIXEL to Vec3d(14.954, 14.000, 47.601) * PIXEL,
            Vec3d(14.954, 0.000, 43.946) * PIXEL to Vec3d(15.954, 20.000, 47.946) * PIXEL,
            Vec3d(-0.046, 0.000, 43.946) * PIXEL to Vec3d(0.954, 20.000, 47.946) * PIXEL,
            Vec3d(0.954, 19.000, 43.946) * PIXEL to Vec3d(14.954, 20.000, 47.946) * PIXEL,
            Vec3d(-8.046, 12.000, 19.946) * PIXEL to Vec3d(-4.046, 26.000, 27.946) * PIXEL,
            Vec3d(12.651, 52.304, 18.946) * PIXEL to Vec3d(22.954, 59.384, 28.946) * PIXEL,
            Vec3d(-7.046, 52.304, 18.946) * PIXEL to Vec3d(3.257, 59.384, 28.946) * PIXEL,
            Vec3d(0.954, 56.000, 16.946) * PIXEL to Vec3d(14.954, 60.000, 30.946) * PIXEL,
            Vec3d(2.954, 55.000, 18.946) * PIXEL to Vec3d(12.954, 56.000, 28.946) * PIXEL,
            Vec3d(5.954, 4.000, 1.946) * PIXEL to Vec3d(9.954, 18.000, 5.946) * PIXEL,
            Vec3d(1.954, 15.382, -0.054) * PIXEL to Vec3d(13.954, 21.702, 7.594) * PIXEL,
            Vec3d(4.954, 0.000, 0.946) * PIXEL to Vec3d(10.954, 4.000, 3.946) * PIXEL,
            Vec3d(-9.046, 56.000, 3.946) * PIXEL to Vec3d(-7.046, 58.000, 5.946) * PIXEL,
            Vec3d(-12.046, 56.000, 6.946) * PIXEL to Vec3d(-10.046, 58.000, 8.946) * PIXEL,
            Vec3d(-12.046, 56.000, 38.946) * PIXEL to Vec3d(-10.046, 58.000, 40.946) * PIXEL,
            Vec3d(-9.046, 56.000, 41.946) * PIXEL to Vec3d(-7.046, 58.000, 43.946) * PIXEL,
            Vec3d(22.954, 56.000, 41.946) * PIXEL to Vec3d(24.954, 58.000, 43.946) * PIXEL,
            Vec3d(25.954, 56.000, 38.946) * PIXEL to Vec3d(27.954, 58.000, 40.946) * PIXEL,
            Vec3d(22.954, 56.000, 3.946) * PIXEL to Vec3d(24.954, 58.000, 5.946) * PIXEL,
            Vec3d(25.954, 56.000, 6.946) * PIXEL to Vec3d(27.954, 58.000, 8.946) * PIXEL,
            Vec3d(27.954, 0.000, 17.946) * PIXEL to Vec3d(31.954, 16.000, 29.946) * PIXEL,
            Vec3d(20.954, 12.000, 17.946) * PIXEL to Vec3d(27.954, 16.000, 29.946) * PIXEL,
            Vec3d(-9.046, 21.000, 21.946) * PIXEL to Vec3d(-8.046, 25.000, 25.946) * PIXEL
    )

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}