package com.cout970.magneticraft.features.multiblocks.structures

import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.rotateBox
import com.cout970.magneticraft.misc.vector.times
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.multiblocks.*
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

object MultiblockSieve : Multiblock() {

    override val name: String = "sieve"
    override val size: BlockPos = BlockPos(3, 2, 5)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos get() = BlockPos(1, 0, 0)

    init {

        val G = grateBlock()
        val B = baseBlock()
        val S = strippedBlock()
        val C = corrugatedIronBlock()
        val P = columnBlock(EnumFacing.UP)
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(listOf(B, B, B), // y = 1
                listOf(S, C, S),
                listOf(S, C, S),
                listOf(S, C, S),
                listOf(S, C, S)),

            zLayers(listOf(B, M, B), // y = 0
                listOf(P, G, P),
                listOf(P, G, P),
                listOf(P, G, P),
                listOf(B, G, B))
        )
    }

    override fun getControllerBlock() = Multiblocks.sieve

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = listOf(
        Vec3d(23.000, 0.000, 41.000) * PIXEL to Vec3d(27.000, 12.000, 45.000) * PIXEL,
        Vec3d(-13.000, 0.000, -30.000) * PIXEL to Vec3d(29.000, 28.000, -20.000) * PIXEL,
        Vec3d(-4.000, 14.950, 0.994) * PIXEL to Vec3d(20.000, 18.245, 22.978) * PIXEL,
        Vec3d(-4.000, 19.587, -20.006) * PIXEL to Vec3d(20.000, 22.882, 1.978) * PIXEL,
        Vec3d(-4.000, 9.950, 20.994) * PIXEL to Vec3d(20.000, 13.245, 42.978) * PIXEL,
        Vec3d(-5.000, 19.483, -21.000) * PIXEL to Vec3d(-4.000, 23.981, 3.077) * PIXEL,
        Vec3d(20.000, 19.483, -21.000) * PIXEL to Vec3d(21.000, 23.981, 3.077) * PIXEL,
        Vec3d(-5.000, 14.856, -21.913) * PIXEL to Vec3d(-4.000, 21.653, 24.044) * PIXEL,
        Vec3d(20.000, 14.856, -21.913) * PIXEL to Vec3d(21.000, 21.653, 24.044) * PIXEL,
        Vec3d(21.000, 0.000, 45.000) * PIXEL to Vec3d(29.000, 14.000, 48.000) * PIXEL,
        Vec3d(-13.000, 0.000, 45.000) * PIXEL to Vec3d(-5.000, 14.000, 48.000) * PIXEL,
        Vec3d(1.000, 26.960, -30.940) * PIXEL to Vec3d(15.000, 32.000, -28.290) * PIXEL,
        Vec3d(1.000, 26.960, -23.710) * PIXEL to Vec3d(15.000, 32.000, -21.060) * PIXEL,
        Vec3d(0.060, 26.960, -30.000) * PIXEL to Vec3d(2.710, 32.000, -22.000) * PIXEL,
        Vec3d(13.290, 26.960, -30.000) * PIXEL to Vec3d(15.940, 32.000, -22.000) * PIXEL,
        Vec3d(0.000, 31.000, -31.000) * PIXEL to Vec3d(1.000, 32.000, -21.000) * PIXEL,
        Vec3d(15.000, 31.000, -31.000) * PIXEL to Vec3d(16.000, 32.000, -21.000) * PIXEL,
        Vec3d(1.000, 31.000, -22.000) * PIXEL to Vec3d(15.000, 32.000, -21.000) * PIXEL,
        Vec3d(1.000, 31.000, -31.000) * PIXEL to Vec3d(15.000, 32.000, -30.000) * PIXEL,
        Vec3d(0.000, 28.000, -22.000) * PIXEL to Vec3d(1.000, 31.000, -21.000) * PIXEL,
        Vec3d(1.000, 28.000, -22.000) * PIXEL to Vec3d(2.000, 31.000, -21.000) * PIXEL,
        Vec3d(14.000, 28.000, -22.000) * PIXEL to Vec3d(15.000, 31.000, -21.000) * PIXEL,
        Vec3d(15.000, 28.000, -22.000) * PIXEL to Vec3d(16.000, 31.000, -21.000) * PIXEL,
        Vec3d(15.000, 28.000, -23.000) * PIXEL to Vec3d(16.000, 31.000, -22.000) * PIXEL,
        Vec3d(0.000, 28.000, -23.000) * PIXEL to Vec3d(1.000, 31.000, -22.000) * PIXEL,
        Vec3d(0.000, 28.000, -30.000) * PIXEL to Vec3d(1.000, 31.000, -29.000) * PIXEL,
        Vec3d(0.000, 28.000, -31.000) * PIXEL to Vec3d(1.000, 31.000, -30.000) * PIXEL,
        Vec3d(1.000, 28.000, -31.000) * PIXEL to Vec3d(2.000, 31.000, -30.000) * PIXEL,
        Vec3d(14.000, 28.000, -31.000) * PIXEL to Vec3d(15.000, 31.000, -30.000) * PIXEL,
        Vec3d(15.000, 28.000, -31.000) * PIXEL to Vec3d(16.000, 31.000, -30.000) * PIXEL,
        Vec3d(15.000, 28.000, -30.000) * PIXEL to Vec3d(16.000, 31.000, -29.000) * PIXEL,
        Vec3d(-2.000, 0.000, -32.000) * PIXEL to Vec3d(18.000, 28.000, -30.000) * PIXEL,
        Vec3d(-1.000, 25.342, -20.588) * PIXEL to Vec3d(17.000, 26.860, -15.511) * PIXEL,
        Vec3d(-1.000, 22.359, -20.901) * PIXEL to Vec3d(-0.000, 25.865, -15.615) * PIXEL,
        Vec3d(16.000, 22.359, -20.901) * PIXEL to Vec3d(17.000, 25.865, -15.615) * PIXEL,
        Vec3d(-0.000, 22.672, -19.907) * PIXEL to Vec3d(16.000, 25.761, -18.599) * PIXEL,
        Vec3d(-7.000, 0.000, 41.000) * PIXEL to Vec3d(23.000, 2.000, 45.000) * PIXEL,
        Vec3d(-7.000, 0.000, 14.000) * PIXEL to Vec3d(23.000, 2.000, 18.000) * PIXEL,
        Vec3d(-7.000, 0.000, -2.000) * PIXEL to Vec3d(23.000, 2.000, 2.000) * PIXEL,
        Vec3d(-7.000, 0.000, -20.000) * PIXEL to Vec3d(23.000, 2.000, -16.000) * PIXEL,
        Vec3d(23.000, 0.000, 14.000) * PIXEL to Vec3d(27.000, 15.000, 18.000) * PIXEL,
        Vec3d(23.000, 0.000, -20.000) * PIXEL to Vec3d(27.000, 23.000, -16.000) * PIXEL,
        Vec3d(23.000, 0.000, -2.000) * PIXEL to Vec3d(27.000, 19.000, 2.000) * PIXEL,
        Vec3d(-11.000, 0.000, -20.000) * PIXEL to Vec3d(-7.000, 23.000, -16.000) * PIXEL,
        Vec3d(-11.000, 0.000, -2.000) * PIXEL to Vec3d(-7.000, 19.000, 2.000) * PIXEL,
        Vec3d(-11.000, 0.000, 14.000) * PIXEL to Vec3d(-7.000, 15.000, 18.000) * PIXEL,
        Vec3d(-11.000, 0.000, 41.000) * PIXEL to Vec3d(-7.000, 12.000, 45.000) * PIXEL,
        Vec3d(4.000, 0.000, 20.000) * PIXEL to Vec3d(12.000, 2.000, 28.000) * PIXEL,
        Vec3d(4.000, 0.000, 4.000) * PIXEL to Vec3d(12.000, 2.000, 12.000) * PIXEL,
        Vec3d(4.000, 0.000, -12.000) * PIXEL to Vec3d(12.000, 2.000, -4.000) * PIXEL,
        Vec3d(-7.000, 9.000, 2.000) * PIXEL to Vec3d(23.000, 11.000, 23.000) * PIXEL,
        Vec3d(-7.000, 9.000, -20.000) * PIXEL to Vec3d(23.000, 13.000, 2.000) * PIXEL,
        Vec3d(1.500, 27.200, -29.000) * PIXEL to Vec3d(14.500, 28.200, -23.000) * PIXEL,
        Vec3d(-7.000, 0.000, 30.000) * PIXEL to Vec3d(23.000, 2.000, 34.000) * PIXEL,
        Vec3d(23.000, 0.000, 30.000) * PIXEL to Vec3d(27.000, 13.000, 34.000) * PIXEL,
        Vec3d(-11.000, 0.000, 30.000) * PIXEL to Vec3d(-7.000, 13.000, 34.000) * PIXEL,
        Vec3d(20.000, 9.765, -21.913) * PIXEL to Vec3d(21.000, 18.653, 43.935) * PIXEL,
        Vec3d(-5.000, 9.765, -21.913) * PIXEL to Vec3d(-4.000, 18.653, 43.935) * PIXEL,
        Vec3d(4.000, 2.000, 18.000) * PIXEL to Vec3d(12.000, 5.000, 30.000) * PIXEL,
        Vec3d(-12.000, 7.993, -20.766) * PIXEL to Vec3d(-6.000, 26.901, 46.440) * PIXEL,
        Vec3d(22.000, 7.993, -20.766) * PIXEL to Vec3d(28.000, 26.901, 46.440) * PIXEL,
        Vec3d(-7.000, 5.000, -20.000) * PIXEL to Vec3d(23.000, 9.000, 45.000) * PIXEL,
        Vec3d(4.000, 2.000, 2.000) * PIXEL to Vec3d(12.000, 5.000, 14.000) * PIXEL,
        Vec3d(4.000, 2.000, -14.000) * PIXEL to Vec3d(12.000, 5.000, -2.000) * PIXEL,
        Vec3d(-10.000, 22.000, -32.000) * PIXEL to Vec3d(-6.000, 26.000, -30.000) * PIXEL,
        Vec3d(22.000, 22.000, -32.000) * PIXEL to Vec3d(26.000, 26.000, -30.000) * PIXEL
    ).map { EnumFacing.NORTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 2) }

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}