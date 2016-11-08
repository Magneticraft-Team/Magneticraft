package com.cout970.magneticraft.multiblock.impl

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.times
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

        val B: IMultiblockComponent = SingleBlockComponent(BlockBurntLimestone.defaultState.withProperty(BlockBurntLimestone.LIMESTONE_STATES, BlockLimestone.LimestoneStates.BRICK), replacement)

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
                        listOf(B, B, B, B, B),
                        listOf(B, B, B, B, B),
                        listOf(B, B, B, B, B),
                        listOf(A, B, B, B, A)),
                zLayers(listOf(B, B, D, B, B),
                        listOf(B, A, A, A, B),
                        listOf(B, A, A, A, B),
                        listOf(B, A, A, A, B),
                        listOf(B, B, B, B, B)),
                zLayers(listOf(B, B, M, B, B),
                        listOf(B, A, A, A, B),
                        listOf(B, A, A, A, B),
                        listOf(B, A, A, A, B),
                        listOf(B, B, B, B, B))
        )
    }

    override fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf(
            Vec3d(-14.0, 4.0, 1.0) * PIXEL to Vec3d(-3.0, 20.0, 15.0) * PIXEL,
            Vec3d(2.0, 0.0, 2.0) * PIXEL to Vec3d(14.0, 14.0, 14.0) * PIXEL,
            Vec3d(2.0, 24.0, 2.0) * PIXEL to Vec3d(14.0, 42.0, 3.0) * PIXEL,
            Vec3d(0.0, 24.0, 3.0) * PIXEL to Vec3d(3.0, 42.0, 4.0) * PIXEL,
            Vec3d(0.0, 24.0, 12.0) * PIXEL to Vec3d(3.0, 42.0, 13.0) * PIXEL,
            Vec3d(2.0, 24.0, 13.0) * PIXEL to Vec3d(14.0, 42.0, 14.0) * PIXEL,
            Vec3d(-7.0, 25.0, 0.9000000953674316) * PIXEL to Vec3d(23.0, 28.0, 3.9000000953674316) * PIXEL,
            Vec3d(-6.0, 24.0, 0.0) * PIXEL to Vec3d(-3.0, 29.0, 4.0) * PIXEL,
            Vec3d(-11.0, 1.0, 3.0) * PIXEL to Vec3d(-1.0, 32.0, 13.0) * PIXEL,
            Vec3d(15.0, 26.0, 4.0) * PIXEL to Vec3d(25.0, 54.0, 12.0) * PIXEL,
            Vec3d(19.0, 24.0, 0.0) * PIXEL to Vec3d(22.0, 29.0, 4.0) * PIXEL,
            Vec3d(19.0, 4.0, 1.0) * PIXEL to Vec3d(30.0, 20.0, 15.0) * PIXEL,
            Vec3d(17.0, 1.0, 3.0) * PIXEL to Vec3d(27.0, 32.0, 13.0) * PIXEL,
            Vec3d(-11.0, 54.0, 2.0) * PIXEL to Vec3d(27.0, 62.0, 14.0) * PIXEL,
            Vec3d(-9.0, 26.0, 4.0) * PIXEL to Vec3d(1.0, 54.0, 12.0) * PIXEL,
            Vec3d(-16.0, 0.0, 0.0) * PIXEL to Vec3d(-2.0, 4.0, 16.0) * PIXEL,
            Vec3d(18.0, 0.0, 0.0) * PIXEL to Vec3d(32.0, 4.0, 16.0) * PIXEL,
            Vec3d(-7.0, 25.0, 12.099999904632568) * PIXEL to Vec3d(23.0, 28.0, 15.099999904632568) * PIXEL,
            Vec3d(19.0, 24.0, 12.0) * PIXEL to Vec3d(22.0, 29.0, 16.0) * PIXEL,
            Vec3d(-6.0, 24.0, 12.0) * PIXEL to Vec3d(-3.0, 29.0, 16.0) * PIXEL,
            Vec3d(13.0, 24.0, 12.0) * PIXEL to Vec3d(16.0, 42.0, 13.0) * PIXEL,
            Vec3d(13.0, 24.0, 3.0) * PIXEL to Vec3d(16.0, 42.0, 4.0) * PIXEL,
            Vec3d(-2.0, 0.0, 3.0) * PIXEL to Vec3d(-1.0, 1.0, 13.0) * PIXEL,
            Vec3d(17.0, 0.0, 3.0) * PIXEL to Vec3d(18.0, 1.0, 13.0) * PIXEL,
            Vec3d(27.0, 18.0, 4.0) * PIXEL to Vec3d(31.0, 30.0, 12.0) * PIXEL,
            Vec3d(31.0, 22.0, 6.0) * PIXEL to Vec3d(32.0, 26.0, 10.0) * PIXEL,
            Vec3d(3.0, 22.0, 3.0) * PIXEL to Vec3d(13.0, 36.0, 13.0) * PIXEL,
            Vec3d(6.5, 36.0, 6.5) * PIXEL to Vec3d(9.5, 64.0, 9.5) * PIXEL
    )

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = listOf()
}