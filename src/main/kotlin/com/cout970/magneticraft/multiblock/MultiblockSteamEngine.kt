package com.cout970.magneticraft.multiblock

import com.cout970.magneticraft.block.MultiblockParts
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.multiblock.components.IgnoreBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.multiblock.core.BlockData
import com.cout970.magneticraft.multiblock.core.IMultiblockComponent
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.multiblock.core.MultiblockContext
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.rotateBox
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec3Of
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 2017/07/17.
 */
object MultiblockSteamEngine : Multiblock() {

    override val name: String = "steam_engine"
    override val size: BlockPos = BlockPos(3, 4, 4)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(0, 1, 0)

    init {
        val replacement = Multiblocks.gap.defaultState

        val I = IgnoreBlockComponent
        val C = SingleBlockComponent(MultiblockParts.column.defaultState, replacement)
        val B = SingleBlockComponent(Blocks.BRICK_BLOCK.defaultState, replacement)
        val vBlock = MultiblockParts.PartType.COPPER_COIL.getBlockState(MultiblockParts.parts)
        val V = SingleBlockComponent(vBlock, replacement)
        val gBlock = MultiblockParts.PartType.GRATE.getBlockState(MultiblockParts.parts)
        val G = SingleBlockComponent(gBlock, replacement)
        val nBlock = MultiblockParts.PartType.BASE.getBlockState(MultiblockParts.parts)
        val N = SingleBlockComponent(nBlock, replacement)
        val oBlock = MultiblockParts.PartType.CORRUGATED_IRON.getBlockState(MultiblockParts.parts)
        val O = SingleBlockComponent(oBlock, replacement)

        val M: IMultiblockComponent = MainBlockComponent(Multiblocks.steamEngine) { context, activate ->
            Multiblocks.MultiblockOrientation.of(context.facing, activate).getBlockState(Multiblocks.steamEngine)
        }

        scheme = yLayers(
                zLayers(listOf(N, I, I), // y = 2
                        listOf(N, I, I),
                        listOf(N, I, I),
                        listOf(I, I, I)),

                zLayers(listOf(O, I, I), // y = 1
                        listOf(C, I, I),
                        listOf(G, G, I),
                        listOf(I, G, I)),

                zLayers(listOf(M, I, I), // y = 0
                        listOf(B, I, I),
                        listOf(B, G, V),
                        listOf(B, G, C)),

                zLayers(listOf(I, I, I), // y = -1
                        listOf(I, I, I),
                        listOf(I, G, I),
                        listOf(I, G, I))
        )
    }

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()

    val hitbox = listOf(
            Vec3d(19.500, 6.000, 17.500) * PIXEL to Vec3d(28.500, 7.000, 26.500) * PIXEL,
            Vec3d(20.000, 7.000, 18.000) * PIXEL to Vec3d(28.000, 19.000, 26.000) * PIXEL,
            Vec3d(22.500, 20.000, 20.500) * PIXEL to Vec3d(25.500, 22.000, 23.500) * PIXEL,
            Vec3d(19.500, 19.000, 17.500) * PIXEL to Vec3d(28.500, 20.000, 26.500) * PIXEL,
            Vec3d(16.000, 0.000, -24.000) * PIXEL to Vec3d(32.000, 4.000, 32.000) * PIXEL,
            Vec3d(18.000, 4.000, 16.000) * PIXEL to Vec3d(30.000, 6.000, 28.000) * PIXEL,
            Vec3d(20.000, 32.000, 0.000) * PIXEL to Vec3d(28.000, 34.000, 8.000) * PIXEL,
            Vec3d(21.000, 34.000, 2.000) * PIXEL to Vec3d(22.000, 39.000, 6.000) * PIXEL,
            Vec3d(26.000, 34.000, 2.000) * PIXEL to Vec3d(27.000, 39.000, 6.000) * PIXEL,
            Vec3d(22.000, 36.000, -15.000) * PIXEL to Vec3d(26.000, 40.000, 23.000) * PIXEL,
            Vec3d(11.000, 6.000, -18.000) * PIXEL to Vec3d(16.000, 10.000, -14.000) * PIXEL,
            Vec3d(23.500, 14.000, 21.500) * PIXEL to Vec3d(24.500, 34.000, 22.500) * PIXEL,
            Vec3d(23.000, 33.000, 21.000) * PIXEL to Vec3d(25.000, 36.000, 23.000) * PIXEL,
            Vec3d(0.000, 7.000, -17.000) * PIXEL to Vec3d(11.000, 9.000, -15.000) * PIXEL,
            Vec3d(-1.000, 5.000, -19.000) * PIXEL to Vec3d(0.000, 11.000, -13.000) * PIXEL,
            Vec3d(16.000, 4.000, -19.000) * PIXEL to Vec3d(20.000, 11.000, -13.000) * PIXEL,
            Vec3d(21.000, 13.084, -16.414) * PIXEL to Vec3d(22.000, 35.087, -13.500) * PIXEL,
            Vec3d(21.000, 35.000, -15.000) * PIXEL to Vec3d(22.000, 38.000, -13.000) * PIXEL,
            Vec3d(20.000, 8.000, -17.000) * PIXEL to Vec3d(21.000, 14.000, -15.000) * PIXEL,
            Vec3d(20.000, 4.000, 29.000) * PIXEL to Vec3d(28.000, 12.000, 32.000) * PIXEL,
            Vec3d(-16.000, 1.000, -32.000) * PIXEL to Vec3d(0.000, 5.000, -6.000) * PIXEL,
            Vec3d(-5.000, 5.000, -30.000) * PIXEL to Vec3d(-1.000, 16.000, -8.000) * PIXEL,
            Vec3d(-10.000, 12.000, -10.000) * PIXEL to Vec3d(-6.000, 17.000, -6.000) * PIXEL,
            Vec3d(-14.000, 1.000, -8.000) * PIXEL to Vec3d(-2.000, 13.000, 0.000) * PIXEL,
            Vec3d(21.000, 4.000, 26.000) * PIXEL to Vec3d(27.000, 11.000, 29.000) * PIXEL,
            Vec3d(23.000, 4.000, -23.000) * PIXEL to Vec3d(31.000, 10.000, -5.000) * PIXEL,
            Vec3d(23.500, 6.000, 13.500) * PIXEL to Vec3d(24.500, 34.000, 14.500) * PIXEL,
            Vec3d(22.500, 33.000, 13.000) * PIXEL to Vec3d(23.500, 36.000, 15.000) * PIXEL,
            Vec3d(24.500, 33.000, 13.000) * PIXEL to Vec3d(25.500, 36.000, 15.000) * PIXEL,
            Vec3d(22.500, 33.000, 15.000) * PIXEL to Vec3d(23.500, 34.000, 22.000) * PIXEL,
            Vec3d(24.500, 33.000, 15.000) * PIXEL to Vec3d(25.500, 34.000, 22.000) * PIXEL,
            Vec3d(23.000, 4.000, 13.000) * PIXEL to Vec3d(25.000, 11.000, 15.000) * PIXEL,
            Vec3d(22.000, 11.000, 26.000) * PIXEL to Vec3d(26.000, 17.000, 29.000) * PIXEL,
            Vec3d(23.500, 15.500, 27.000) * PIXEL to Vec3d(24.500, 19.500, 28.000) * PIXEL,
            Vec3d(20.000, 18.000, 27.000) * PIXEL to Vec3d(28.000, 19.000, 28.000) * PIXEL,
            Vec3d(20.000, 4.000, 27.000) * PIXEL to Vec3d(21.000, 18.000, 28.000) * PIXEL,
            Vec3d(18.000, 6.000, -14.070) * PIXEL to Vec3d(19.000, 9.325, 23.977) * PIXEL,
            Vec3d(18.500, 5.000, 23.000) * PIXEL to Vec3d(19.500, 9.000, 25.000) * PIXEL,
            Vec3d(27.000, 4.000, 27.000) * PIXEL to Vec3d(28.000, 18.000, 28.000) * PIXEL,
            Vec3d(-15.000, 5.000, -12.000) * PIXEL to Vec3d(-5.000, 16.000, -8.000) * PIXEL,
            Vec3d(-15.000, 5.000, -30.000) * PIXEL to Vec3d(-5.000, 16.000, -24.000) * PIXEL,
            Vec3d(-9.000, 10.000, -24.000) * PIXEL to Vec3d(-8.000, 11.000, -12.000) * PIXEL,
            Vec3d(-15.000, 5.000, -24.000) * PIXEL to Vec3d(-13.000, 8.000, -12.000) * PIXEL,
            Vec3d(-13.000, 5.000, -24.000) * PIXEL to Vec3d(-5.000, 6.000, -12.000) * PIXEL,
            Vec3d(11.500, 2.000, -5.000) * PIXEL to Vec3d(14.500, 14.000, -2.000) * PIXEL,
            Vec3d(11.500, 19.000, -22.000) * PIXEL to Vec3d(14.500, 22.000, -10.000) * PIXEL,
            Vec3d(11.500, 12.031, -29.930) * PIXEL to Vec3d(14.500, 21.930, -20.031) * PIXEL,
            Vec3d(11.500, -6.000, -22.000) * PIXEL to Vec3d(14.500, -3.000, -10.000) * PIXEL,
            Vec3d(11.500, 12.031, -11.969) * PIXEL to Vec3d(14.500, 21.930, -2.070) * PIXEL,
            Vec3d(11.500, -5.930, -29.930) * PIXEL to Vec3d(14.500, 3.969, -20.031) * PIXEL,
            Vec3d(11.500, -5.930, -11.969) * PIXEL to Vec3d(14.500, 3.969, -2.070) * PIXEL,
            Vec3d(11.500, 2.000, -30.000) * PIXEL to Vec3d(14.500, 14.000, -27.000) * PIXEL,
            Vec3d(12.000, -3.469, -21.516) * PIXEL to Vec3d(14.000, 19.469, -10.484) * PIXEL,
            Vec3d(12.000, -3.469, -21.516) * PIXEL to Vec3d(14.000, 19.469, -10.484) * PIXEL,
            Vec3d(12.000, 2.484, -27.469) * PIXEL to Vec3d(14.000, 13.516, -4.531) * PIXEL,
            Vec3d(12.000, 2.484, -27.469) * PIXEL to Vec3d(14.000, 13.516, -4.531) * PIXEL,
            Vec3d(21.000, 10.000, 1.000) * PIXEL to Vec3d(27.000, 32.000, 7.000) * PIXEL,
            Vec3d(20.000, 4.000, 0.000) * PIXEL to Vec3d(28.000, 10.000, 8.000) * PIXEL,
            Vec3d(-15.000, 8.000, -24.000) * PIXEL to Vec3d(-13.000, 14.000, -12.000) * PIXEL,
            Vec3d(-15.000, 14.000, -24.000) * PIXEL to Vec3d(-5.000, 16.000, -12.000) * PIXEL,
            Vec3d(-15.500, 6.000, -19.000) * PIXEL to Vec3d(-14.500, 9.000, -17.000) * PIXEL,
            Vec3d(-10.621, 8.379, -15.000) * PIXEL to Vec3d(-6.379, 12.621, -13.000) * PIXEL,
            Vec3d(-10.621, 8.379, -15.000) * PIXEL to Vec3d(-6.379, 12.621, -13.000) * PIXEL,
            Vec3d(-11.000, 10.000, -15.000) * PIXEL to Vec3d(-6.000, 11.000, -13.000) * PIXEL,
            Vec3d(-9.000, 8.000, -15.000) * PIXEL to Vec3d(-8.000, 13.000, -13.000) * PIXEL,
            Vec3d(-9.919, 2.999, -18.000) * PIXEL to Vec3d(-7.081, 8.001, -16.000) * PIXEL,
            Vec3d(-11.001, 4.081, -18.000) * PIXEL to Vec3d(-5.999, 6.919, -16.000) * PIXEL,
            Vec3d(-11.001, 4.081, -18.000) * PIXEL to Vec3d(-5.999, 6.919, -16.000) * PIXEL,
            Vec3d(-9.919, 2.999, -18.000) * PIXEL to Vec3d(-7.081, 8.001, -16.000) * PIXEL,
            Vec3d(-9.919, 3.999, -21.000) * PIXEL to Vec3d(-7.081, 9.001, -19.000) * PIXEL,
            Vec3d(-11.001, 5.081, -21.000) * PIXEL to Vec3d(-5.999, 7.919, -19.000) * PIXEL,
            Vec3d(-11.001, 5.081, -21.000) * PIXEL to Vec3d(-5.999, 7.919, -19.000) * PIXEL,
            Vec3d(-9.919, 3.999, -21.000) * PIXEL to Vec3d(-7.081, 9.001, -19.000) * PIXEL,
            Vec3d(-10.621, 8.379, -21.000) * PIXEL to Vec3d(-6.379, 12.621, -19.000) * PIXEL,
            Vec3d(-10.621, 8.379, -21.000) * PIXEL to Vec3d(-6.379, 12.621, -19.000) * PIXEL,
            Vec3d(-11.000, 10.000, -21.000) * PIXEL to Vec3d(-6.000, 11.000, -19.000) * PIXEL,
            Vec3d(-9.000, 8.000, -21.000) * PIXEL to Vec3d(-8.000, 13.000, -19.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }

    override fun getGlobalCollisionBox(): List<AxisAlignedBB> = hitbox
}