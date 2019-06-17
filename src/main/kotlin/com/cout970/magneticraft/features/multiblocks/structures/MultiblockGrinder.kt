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


@Suppress("LocalVariableName")
object MultiblockGrinder : Multiblock() {

    override val name: String = "grinder"
    override val size: BlockPos = BlockPos(3, 4, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {

        val E = copperCoilBlock()
        val P = grateBlock()
        val B = baseBlock()
        val S = strippedBlock()
        val C = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(listOf(S, S, S), // y = 3
                listOf(S, B, S),
                listOf(S, S, S)),

            zLayers(listOf(C, P, C), // y = 2
                listOf(P, B, P),
                listOf(C, P, C)),

            zLayers(listOf(C, P, C), // y = 1
                listOf(P, E, P),
                listOf(C, P, C)),

            zLayers(listOf(B, M, B), // y = 0
                listOf(B, B, B),
                listOf(B, B, B))
        )
    }

    override fun getControllerBlock() = Multiblocks.grinder

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitbox

    val hitbox = listOf(
        Vec3d(-15.000, 14.000, -15.000) * PIXEL to Vec3d(-9.000, 58.000, -9.000) * PIXEL,
        Vec3d(25.000, 14.000, -15.000) * PIXEL to Vec3d(31.000, 58.000, -9.000) * PIXEL,
        Vec3d(25.000, 14.000, 25.000) * PIXEL to Vec3d(31.000, 58.000, 31.000) * PIXEL,
        Vec3d(-10.000, 40.624, -12.782) * PIXEL to Vec3d(26.000, 64.000, -1.009) * PIXEL,
        Vec3d(-16.000, 58.000, -16.000) * PIXEL to Vec3d(32.000, 64.000, -10.000) * PIXEL,
        Vec3d(17.009, 40.624, -10.000) * PIXEL to Vec3d(28.782, 64.000, 26.000) * PIXEL,
        Vec3d(26.000, 58.000, -10.000) * PIXEL to Vec3d(32.000, 64.000, 26.000) * PIXEL,
        Vec3d(-15.000, 14.000, 25.000) * PIXEL to Vec3d(-9.000, 58.000, 31.000) * PIXEL,
        Vec3d(-10.000, 40.624, 17.009) * PIXEL to Vec3d(26.000, 64.000, 28.782) * PIXEL,
        Vec3d(-16.000, 58.000, 26.000) * PIXEL to Vec3d(32.000, 64.000, 32.000) * PIXEL,
        Vec3d(-16.000, 58.000, -10.000) * PIXEL to Vec3d(-10.000, 64.000, 26.000) * PIXEL,
        Vec3d(-12.782, 40.624, -10.000) * PIXEL to Vec3d(-1.009, 64.000, 26.000) * PIXEL,
        Vec3d(2.000, 11.000, 2.000) * PIXEL to Vec3d(14.000, 55.000, 14.000) * PIXEL,
        Vec3d(-12.000, 0.000, -12.000) * PIXEL to Vec3d(28.000, 12.000, 28.000) * PIXEL,
        Vec3d(-4.000, 12.000, -4.000) * PIXEL to Vec3d(20.000, 42.000, -2.000) * PIXEL,
        Vec3d(-4.000, 12.000, -2.000) * PIXEL to Vec3d(-2.000, 42.000, 18.000) * PIXEL,
        Vec3d(-4.000, 12.000, 18.000) * PIXEL to Vec3d(20.000, 42.000, 20.000) * PIXEL,
        Vec3d(-16.000, 0.000, 24.000) * PIXEL to Vec3d(-8.000, 14.000, 32.000) * PIXEL,
        Vec3d(-12.000, 40.000, -4.000) * PIXEL to Vec3d(-4.000, 42.000, 20.000) * PIXEL,
        Vec3d(-12.000, 42.000, 23.000) * PIXEL to Vec3d(-7.000, 56.000, 28.000) * PIXEL,
        Vec3d(-12.000, 40.000, 20.000) * PIXEL to Vec3d(28.000, 42.000, 28.000) * PIXEL,
        Vec3d(20.000, 40.000, -4.000) * PIXEL to Vec3d(28.000, 42.000, 20.000) * PIXEL,
        Vec3d(-12.000, 40.000, -12.000) * PIXEL to Vec3d(28.000, 42.000, -4.000) * PIXEL,
        Vec3d(24.000, 0.000, 24.000) * PIXEL to Vec3d(32.000, 14.000, 32.000) * PIXEL,
        Vec3d(18.000, 12.000, -2.000) * PIXEL to Vec3d(20.000, 42.000, 18.000) * PIXEL,
        Vec3d(-16.000, 0.000, -16.000) * PIXEL to Vec3d(-8.000, 14.000, -8.000) * PIXEL,
        Vec3d(24.000, 0.000, -16.000) * PIXEL to Vec3d(32.000, 14.000, -8.000) * PIXEL,
        Vec3d(23.000, 42.000, 23.000) * PIXEL to Vec3d(28.000, 56.000, 28.000) * PIXEL,
        Vec3d(23.000, 42.000, -12.000) * PIXEL to Vec3d(28.000, 56.000, -7.000) * PIXEL,
        Vec3d(-12.000, 42.000, -12.000) * PIXEL to Vec3d(-7.000, 56.000, -7.000) * PIXEL,
        Vec3d(0.000, 12.000, -12.000) * PIXEL to Vec3d(16.000, 20.000, -4.000) * PIXEL,
        Vec3d(1.000, 11.460, -15.654) * PIXEL to Vec3d(15.000, 14.000, -11.593) * PIXEL,
        Vec3d(0.000, 0.000, -16.000) * PIXEL to Vec3d(1.000, 20.000, -12.000) * PIXEL,
        Vec3d(15.000, 0.000, -16.000) * PIXEL to Vec3d(16.000, 20.000, -12.000) * PIXEL,
        Vec3d(1.000, 19.000, -16.000) * PIXEL to Vec3d(15.000, 20.000, -12.000) * PIXEL,
        Vec3d(27.000, 13.000, 4.000) * PIXEL to Vec3d(31.000, 27.000, 12.000) * PIXEL,
        Vec3d(-7.000, 52.304, 3.000) * PIXEL to Vec3d(3.303, 59.384, 13.000) * PIXEL,
        Vec3d(12.697, 52.304, 3.000) * PIXEL to Vec3d(23.000, 59.384, 13.000) * PIXEL,
        Vec3d(1.000, 56.000, 1.000) * PIXEL to Vec3d(15.000, 60.000, 15.000) * PIXEL,
        Vec3d(3.000, 55.000, 3.000) * PIXEL to Vec3d(13.000, 56.000, 13.000) * PIXEL,
        Vec3d(6.000, 4.000, 26.000) * PIXEL to Vec3d(10.000, 18.000, 30.000) * PIXEL,
        Vec3d(2.000, 15.382, 24.352) * PIXEL to Vec3d(14.000, 21.702, 32.000) * PIXEL,
        Vec3d(5.000, 0.000, 28.000) * PIXEL to Vec3d(11.000, 4.000, 31.000) * PIXEL,
        Vec3d(23.000, 56.000, 26.000) * PIXEL to Vec3d(25.000, 58.000, 28.000) * PIXEL,
        Vec3d(26.000, 56.000, 23.000) * PIXEL to Vec3d(28.000, 58.000, 25.000) * PIXEL,
        Vec3d(26.000, 56.000, -9.000) * PIXEL to Vec3d(28.000, 58.000, -7.000) * PIXEL,
        Vec3d(23.000, 56.000, -12.000) * PIXEL to Vec3d(25.000, 58.000, -10.000) * PIXEL,
        Vec3d(-9.000, 56.000, -12.000) * PIXEL to Vec3d(-7.000, 58.000, -10.000) * PIXEL,
        Vec3d(-12.000, 56.000, -9.000) * PIXEL to Vec3d(-10.000, 58.000, -7.000) * PIXEL,
        Vec3d(-9.000, 56.000, 26.000) * PIXEL to Vec3d(-7.000, 58.000, 28.000) * PIXEL,
        Vec3d(-12.000, 56.000, 23.000) * PIXEL to Vec3d(-10.000, 58.000, 25.000) * PIXEL,
        Vec3d(-16.000, 0.000, 2.000) * PIXEL to Vec3d(-12.000, 16.000, 14.000) * PIXEL,
        Vec3d(-12.000, 12.000, 2.000) * PIXEL to Vec3d(-4.000, 16.000, 14.000) * PIXEL,
        Vec3d(31.000, 22.000, 6.000) * PIXEL to Vec3d(32.000, 26.000, 10.000) * PIXEL,
        Vec3d(20.000, 12.000, 2.000) * PIXEL to Vec3d(27.000, 28.000, 14.000) * PIXEL,
        Vec3d(32.000, 23.500, 7.500) * PIXEL to Vec3d(33.000, 24.500, 8.500) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}