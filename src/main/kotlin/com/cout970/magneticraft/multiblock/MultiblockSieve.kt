package com.cout970.magneticraft.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.multiblock.components.IgnoreBlockComponent
import com.cout970.magneticraft.multiblock.core.*
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.rotateBox
import com.cout970.magneticraft.util.vector.times
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

object MultiblockSieve : Multiblock() {

    override val name: String = "sieve"
    override val size: BlockPos = BlockPos(3, 2, 4)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos get() = BlockPos(1, 0, 0)

    init {

        val I = IgnoreBlockComponent
        val E = copperCoilBlock()
        val G = grateBlock()
        val B = baseBlock()
        val S = strippedBlock()
        val C = corrugatedIronBlock()
        val P = collumnBlock()
        val M = mainBlockOf(Multiblocks.sieve)

        scheme = yLayers(
                zLayers(listOf(B, B, B), // y = 1
                        listOf(S, C, S),
                        listOf(S, C, S),
                        listOf(S, C, S)),

                zLayers(listOf(B, M, B), // y = 0
                        listOf(P, G, P),
                        listOf(P, G, P),
                        listOf(B, G, B))
        )
    }

    override fun getControllerBlock() = Multiblocks.shelvingUnit

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = listOf(
            Vec3d(23.000, 0.000, 25.000) * PIXEL to Vec3d(27.000, 12.000, 29.000) * PIXEL,
            Vec3d(-13.000, 0.000, -30.000) * PIXEL to Vec3d(29.000, 28.000, -20.000) * PIXEL,
            Vec3d(-4.000, 15.005, -5.006) * PIXEL to Vec3d(20.000, 17.882, 13.000) * PIXEL,
            Vec3d(-4.000, 20.005, -20.006) * PIXEL to Vec3d(20.000, 22.882, -2.000) * PIXEL,
            Vec3d(-4.000, 10.005, 9.994) * PIXEL to Vec3d(20.000, 12.882, 28.000) * PIXEL,
            Vec3d(-5.000, 19.901, -21.000) * PIXEL to Vec3d(-4.000, 23.981, -0.901) * PIXEL,
            Vec3d(20.000, 19.901, -21.000) * PIXEL to Vec3d(21.000, 23.981, -0.901) * PIXEL,
            Vec3d(-5.000, 14.901, -21.913) * PIXEL to Vec3d(-4.000, 20.653, 14.099) * PIXEL,
            Vec3d(20.000, 14.901, -21.913) * PIXEL to Vec3d(21.000, 20.653, 14.099) * PIXEL,
            Vec3d(-5.000, 10.005, -20.831) * PIXEL to Vec3d(-4.000, 17.221, 29.105) * PIXEL,
            Vec3d(20.000, 10.005, -20.831) * PIXEL to Vec3d(21.000, 17.221, 29.105) * PIXEL,
            Vec3d(22.000, 8.155, -21.972) * PIXEL to Vec3d(28.000, 27.160, 32.000) * PIXEL,
            Vec3d(21.000, 0.000, 29.000) * PIXEL to Vec3d(29.000, 14.000, 32.000) * PIXEL,
            Vec3d(-13.000, 0.000, 29.000) * PIXEL to Vec3d(-5.000, 14.000, 32.000) * PIXEL,
            Vec3d(-12.000, 8.155, -21.972) * PIXEL to Vec3d(-6.000, 27.160, 32.000) * PIXEL,
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
            Vec3d(-7.000, 0.000, 25.000) * PIXEL to Vec3d(23.000, 2.000, 29.000) * PIXEL,
            Vec3d(-7.000, 0.000, 10.000) * PIXEL to Vec3d(23.000, 2.000, 14.000) * PIXEL,
            Vec3d(-7.000, 0.000, -5.000) * PIXEL to Vec3d(23.000, 2.000, -1.000) * PIXEL,
            Vec3d(-7.000, 0.000, -20.000) * PIXEL to Vec3d(23.000, 2.000, -16.000) * PIXEL,
            Vec3d(23.000, 0.000, 10.000) * PIXEL to Vec3d(27.000, 15.000, 14.000) * PIXEL,
            Vec3d(23.000, 0.000, -20.000) * PIXEL to Vec3d(27.000, 23.000, -16.000) * PIXEL,
            Vec3d(23.000, 0.000, -5.000) * PIXEL to Vec3d(27.000, 19.000, -1.000) * PIXEL,
            Vec3d(-11.000, 0.000, -20.000) * PIXEL to Vec3d(-7.000, 23.000, -16.000) * PIXEL,
            Vec3d(-11.000, 0.000, -5.000) * PIXEL to Vec3d(-7.000, 19.000, -1.000) * PIXEL,
            Vec3d(-11.000, 0.000, 10.000) * PIXEL to Vec3d(-7.000, 15.000, 14.000) * PIXEL,
            Vec3d(-11.000, 0.000, 25.000) * PIXEL to Vec3d(-7.000, 12.000, 29.000) * PIXEL,
            Vec3d(-7.000, 5.000, -20.000) * PIXEL to Vec3d(23.000, 9.000, 29.000) * PIXEL,
            Vec3d(4.000, 3.000, 16.000) * PIXEL to Vec3d(12.000, 5.000, 24.000) * PIXEL,
            Vec3d(4.000, 3.000, 1.000) * PIXEL to Vec3d(12.000, 5.000, 9.000) * PIXEL,
            Vec3d(4.000, 3.000, -14.000) * PIXEL to Vec3d(12.000, 5.000, -6.000) * PIXEL,
            Vec3d(-7.000, 9.000, -3.000) * PIXEL to Vec3d(23.000, 11.000, 12.000) * PIXEL,
            Vec3d(-7.000, 9.000, -20.000) * PIXEL to Vec3d(23.000, 13.000, -3.000) * PIXEL,
            Vec3d(1.500, 27.200, -29.000) * PIXEL to Vec3d(14.500, 28.200, -23.000) * PIXEL
    ).map { EnumFacing.NORTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 2) }

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}