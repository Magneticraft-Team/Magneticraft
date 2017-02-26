package com.cout970.magneticraft.multiblock.impl


import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_CENTER
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.block.decoration.BlockBurntLimestone
import com.cout970.magneticraft.block.decoration.BlockLimestone
import com.cout970.magneticraft.block.decoration.BlockMachineBlock
import com.cout970.magneticraft.block.multiblock.BlockKiln
import com.cout970.magneticraft.multiblock.BlockData
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.tilerenderer.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.times
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 20/08/2016.
 */
object MultiblockKiln : Multiblock() {

    override val name: String = "kiln"
    override val size: BlockPos = BlockPos(5, 3, 5)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(2, 0, 0)

    init {
        val replacement = BlockKiln.defaultState
                .withProperty(PROPERTY_CENTER, false)
                .withProperty(PROPERTY_ACTIVE, true)

        val A: IMultiblockComponent = SingleBlockComponent(
                Blocks.AIR.defaultState, Blocks.AIR.defaultState)

        val B: IMultiblockComponent = SingleBlockComponent(
                BlockBurntLimestone.defaultState
                        .withProperty(BlockBurntLimestone.LIMESTONE_STATES, BlockLimestone.LimestoneStates.BRICK),
                replacement)

        val L: IMultiblockComponent = SingleBlockComponent(
                BlockBurntLimestone.defaultState
                        .withProperty(BlockBurntLimestone.LIMESTONE_STATES, BlockLimestone.LimestoneStates.NORMAL),
                replacement)

        val D: IMultiblockComponent = SingleBlockComponent(BlockMachineBlock.defaultState, replacement)

        val M: IMultiblockComponent = MainBlockComponent(BlockKiln) { context, state, activate ->
            if (activate) {
                BlockKiln.defaultState
                        .withProperty(PROPERTY_ACTIVE, true)
                        .withProperty(PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            } else {
                BlockKiln.defaultState
                        .withProperty(PROPERTY_ACTIVE, false)
                        .withProperty(PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            }
        }

        scheme = yLayers(
                zLayers(listOf(A, B, B, B, A),
                        listOf(B, L, B, L, B),
                        listOf(B, B, B, B, B),
                        listOf(B, L, B, L, B),
                        listOf(A, B, B, B, A)),
                zLayers(listOf(L, B, D, B, L),
                        listOf(B, A, A, A, B),
                        listOf(L, A, A, A, L),
                        listOf(B, A, A, A, B),
                        listOf(L, B, L, B, L)),
                zLayers(listOf(L, B, M, B, L),
                        listOf(B, A, A, A, B),
                        listOf(L, A, A, A, L),
                        listOf(B, A, A, A, B),
                        listOf(L, B, L, B, L))
        )
    }

    //@formatter:off
    override fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf(
            Vec3d(-24.0, 0.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-16.0, 24.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(32.0, 0.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(40.0, 24.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-22.0, 24.0, -22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-16.0, 40.0, 38.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(32.0, 24.0, -22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(38.0, 40.0, 38.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-16.0, 0.0, 32.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(32.0, 24.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-16.0, 24.0, 32.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(32.0, 40.0, 38.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-20.0, 40.0, -20.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(36.0, 44.0, 36.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-24.0, -1.0, -12.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-19.0, 44.0, -6.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(0.0, 0.0, -20.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(16.0, 32.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-24.0, 44.0, -12.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(40.0, 46.0, -6.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-24.0, -1.0, 22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-19.0, 44.0, 28.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-24.0, 44.0, 22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(40.0, 46.0, 28.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-13.0, 44.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(29.0, 47.0, 32.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(35.0, -1.0, -12.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(40.0, 44.0, -6.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(35.0, -1.0, 22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(40.0, 44.0, 28.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-16.0, 0.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(0.0, 24.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-16.0, 24.0, -22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(0.0, 40.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(16.0, 0.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(32.0, 24.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(16.0, 24.0, -22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(32.0, 40.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(0.0, 32.0, -22.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(16.0, 40.0, -16.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-10.0, -1.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-4.0, 44.0, -19.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-10.0, 44.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-4.0, 46.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(20.0, -1.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(26.0, 44.0, -19.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(20.0, 44.0, -24.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(26.0, 46.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(-10.0, -1.0, 35.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(-4.0, 44.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0),
            Vec3d(20.0, -1.0, 35.0) * PIXEL + Vec3d(0.0, 0.0, 2.0) to Vec3d(26.0, 44.0, 40.0) * PIXEL + Vec3d(0.0, 0.0, 2.0)
    )
    //@formatter:on

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}