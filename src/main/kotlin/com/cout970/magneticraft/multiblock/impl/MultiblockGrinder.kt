package com.cout970.magneticraft.multiblock.impl

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.times
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

        val P: IMultiblockComponent = ContextBlockComponent(
                { ctx ->
                    BlockMachineBlockSupportColumn.defaultState.withProperty(BlockMachineBlockSupportColumn.PROPERTY_STATES,
                            BlockMachineBlockSupportColumn.States.fromAxis(ctx.facing.rotateY().axis))
                }, ItemStack(BlockMachineBlockSupportColumn, 1, 1), replacement)

        val O: IMultiblockComponent = ContextBlockComponent(
                { ctx ->
                    BlockMachineBlockSupportColumn.defaultState.withProperty(BlockMachineBlockSupportColumn.PROPERTY_STATES,
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
            Vec3d(-15.0, 14.0, 25.0) * PIXEL to Vec3d(-9.0, 58.0, 31.0) * PIXEL,
            Vec3d(25.0, 14.0, 25.0) * PIXEL to Vec3d(31.0, 58.0, 31.0) * PIXEL,
            Vec3d(25.0, 14.0, -15.0) * PIXEL to Vec3d(31.0, 58.0, -9.0) * PIXEL,
            Vec3d(-10.0, 40.0, 26.0) * PIXEL to Vec3d(26.0, 64.0, 29.0) * PIXEL,
            Vec3d(-16.0, 58.0, 26.0) * PIXEL to Vec3d(32.0, 64.0, 32.0) * PIXEL,
            Vec3d(26.0, 40.0, -10.0) * PIXEL to Vec3d(29.0, 64.0, 26.0) * PIXEL,
            Vec3d(26.0, 58.0, -10.0) * PIXEL to Vec3d(32.0, 64.0, 26.0) * PIXEL,
            Vec3d(-15.0, 14.0, -15.0) * PIXEL to Vec3d(-9.0, 58.0, -9.0) * PIXEL,
            Vec3d(-10.0, 40.0, -13.0) * PIXEL to Vec3d(26.0, 64.0, -10.0) * PIXEL,
            Vec3d(-16.0, 58.0, -16.0) * PIXEL to Vec3d(32.0, 64.0, -10.0) * PIXEL,
            Vec3d(-16.0, 58.0, -10.0) * PIXEL to Vec3d(-10.0, 64.0, 26.0) * PIXEL,
            Vec3d(-13.0, 40.0, -10.0) * PIXEL to Vec3d(-10.0, 64.0, 26.0) * PIXEL,
            Vec3d(2.0, 11.0, 2.0) * PIXEL to Vec3d(14.0, 55.0, 14.0) * PIXEL,
            Vec3d(-12.0, 0.0, -12.0) * PIXEL to Vec3d(28.0, 12.0, 28.0) * PIXEL,
            Vec3d(-4.0, 12.0, 18.0) * PIXEL to Vec3d(20.0, 42.0, 20.0) * PIXEL,
            Vec3d(-4.0, 12.0, -2.0) * PIXEL to Vec3d(-2.0, 42.0, 18.0) * PIXEL,
            Vec3d(-4.0, 12.0, -4.0) * PIXEL to Vec3d(20.0, 42.0, -2.0) * PIXEL,
            Vec3d(-16.0, 0.0, -16.0) * PIXEL to Vec3d(-8.0, 14.0, -8.0) * PIXEL,
            Vec3d(-12.0, 40.0, -4.0) * PIXEL to Vec3d(-4.0, 42.0, 20.0) * PIXEL,
            Vec3d(-12.0, 42.0, -12.0) * PIXEL to Vec3d(-7.0, 56.0, -7.0) * PIXEL,
            Vec3d(-12.0, 40.0, -12.0) * PIXEL to Vec3d(28.0, 42.0, -4.0) * PIXEL,
            Vec3d(20.0, 40.0, -4.0) * PIXEL to Vec3d(28.0, 42.0, 20.0) * PIXEL,
            Vec3d(-12.0, 40.0, 20.0) * PIXEL to Vec3d(28.0, 42.0, 28.0) * PIXEL,
            Vec3d(24.0, 0.0, -16.0) * PIXEL to Vec3d(32.0, 14.0, -8.0) * PIXEL,
            Vec3d(18.0, 12.0, -2.0) * PIXEL to Vec3d(20.0, 42.0, 18.0) * PIXEL,
            Vec3d(-16.0, 0.0, 24.0) * PIXEL to Vec3d(-8.0, 14.0, 32.0) * PIXEL,
            Vec3d(24.0, 0.0, 24.0) * PIXEL to Vec3d(32.0, 14.0, 32.0) * PIXEL,
            Vec3d(23.0, 42.0, -12.0) * PIXEL to Vec3d(28.0, 56.0, -7.0) * PIXEL,
            Vec3d(23.0, 42.0, 23.0) * PIXEL to Vec3d(28.0, 56.0, 28.0) * PIXEL,
            Vec3d(-12.0, 42.0, 23.0) * PIXEL to Vec3d(-7.0, 56.0, 28.0) * PIXEL,
            Vec3d(0.0, 12.0, 20.0) * PIXEL to Vec3d(16.0, 20.0, 28.0) * PIXEL,
            Vec3d(1.0, 13.0, 28.0) * PIXEL to Vec3d(15.0, 14.0, 32.0) * PIXEL,
            Vec3d(0.0, 0.0, 28.0) * PIXEL to Vec3d(1.0, 20.0, 32.0) * PIXEL,
            Vec3d(15.0, 0.0, 28.0) * PIXEL to Vec3d(16.0, 20.0, 32.0) * PIXEL,
            Vec3d(1.0, 19.0, 28.0) * PIXEL to Vec3d(15.0, 20.0, 32.0) * PIXEL,
            Vec3d(20.0, 12.0, 4.0) * PIXEL to Vec3d(24.0, 26.0, 12.0) * PIXEL,
            Vec3d(-7.0, 52.0, 3.0) * PIXEL to Vec3d(3.0, 55.0, 13.0) * PIXEL,
            Vec3d(13.0, 52.0, 3.0) * PIXEL to Vec3d(23.0, 55.0, 13.0) * PIXEL,
            Vec3d(1.0, 56.0, 1.0) * PIXEL to Vec3d(15.0, 60.0, 15.0) * PIXEL,
            Vec3d(3.0, 55.0, 3.0) * PIXEL to Vec3d(13.0, 56.0, 13.0) * PIXEL,
            Vec3d(6.0, 4.0, -14.0) * PIXEL to Vec3d(10.0, 18.0, -10.0) * PIXEL,
            Vec3d(2.0, 15.0, -16.0) * PIXEL to Vec3d(14.0, 17.0, -8.0) * PIXEL,
            Vec3d(5.0, 0.0, -15.0) * PIXEL to Vec3d(11.0, 4.0, -12.0) * PIXEL,
            Vec3d(23.0, 56.0, -12.0) * PIXEL to Vec3d(25.0, 58.0, -10.0) * PIXEL,
            Vec3d(26.0, 56.0, -9.0) * PIXEL to Vec3d(28.0, 58.0, -7.0) * PIXEL,
            Vec3d(26.0, 56.0, 23.0) * PIXEL to Vec3d(28.0, 58.0, 25.0) * PIXEL,
            Vec3d(23.0, 56.0, 26.0) * PIXEL to Vec3d(25.0, 58.0, 28.0) * PIXEL,
            Vec3d(-9.0, 56.0, 26.0) * PIXEL to Vec3d(-7.0, 58.0, 28.0) * PIXEL,
            Vec3d(-12.0, 56.0, 23.0) * PIXEL to Vec3d(-10.0, 58.0, 25.0) * PIXEL,
            Vec3d(-9.0, 56.0, -12.0) * PIXEL to Vec3d(-7.0, 58.0, -10.0) * PIXEL,
            Vec3d(-12.0, 56.0, -9.0) * PIXEL to Vec3d(-10.0, 58.0, -7.0) * PIXEL,
            Vec3d(-16.0, 0.0, 2.0) * PIXEL to Vec3d(-12.0, 16.0, 14.0) * PIXEL,
            Vec3d(-12.0, 12.0, 2.0) * PIXEL to Vec3d(-5.0, 16.0, 14.0) * PIXEL,
            Vec3d(24.0, 21.0, 6.0) * PIXEL to Vec3d(25.0, 25.0, 10.0) * PIXEL
    )

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = listOf()
}