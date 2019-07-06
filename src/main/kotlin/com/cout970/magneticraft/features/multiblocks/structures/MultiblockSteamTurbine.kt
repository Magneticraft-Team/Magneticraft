package com.cout970.magneticraft.features.multiblocks.structures

import com.cout970.magneticraft.features.fluid_machines.Blocks
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

object MultiblockSteamTurbine : Multiblock() {

    override val name: String = "steam_turbine"
    override val size: BlockPos = BlockPos(3, 3, 5)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val I = grateBlock()
        val B = corrugatedIronBlock()
        val C = copperCoilBlock()
        val A = baseBlock()
        val T = ofBlock(Blocks.smallTank)
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(A, I, A), // y = 2
                listOf(I, C, I),
                listOf(I, I, I),
                listOf(I, I, I),
                listOf(A, I, A)),

            zLayers(
                listOf(I, A, I), // y = 1
                listOf(T, B, T),
                listOf(T, B, T),
                listOf(A, B, A),
                listOf(I, I, I)),

            zLayers(
                listOf(A, M, A), // y = 0
                listOf(I, I, I),
                listOf(I, I, I),
                listOf(I, I, I),
                listOf(A, I, A))
        )
    }

    override fun getControllerBlock() = Multiblocks.steamTurbine

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(0.000, 0.000, -37.000) * PIXEL to Vec3d(48.000, 8.000, 16.000) * PIXEL,
        Vec3d(2.000, 0.000, -64.000) * PIXEL to Vec3d(46.000, 2.000, -48.000) * PIXEL,
        Vec3d(0.000, 0.000, -64.000) * PIXEL to Vec3d(2.000, 46.000, -48.000) * PIXEL,
        Vec3d(46.000, 0.000, -64.000) * PIXEL to Vec3d(48.000, 46.000, -48.000) * PIXEL,
        Vec3d(0.000, 46.000, -64.000) * PIXEL to Vec3d(48.000, 48.000, -48.000) * PIXEL,
        Vec3d(20.000, 20.000, -61.000) * PIXEL to Vec3d(28.000, 28.000, -48.000) * PIXEL,
        Vec3d(2.000, 2.000, -49.000) * PIXEL to Vec3d(46.000, 46.000, -48.000) * PIXEL,
        Vec3d(0.000, 8.000, -14.000) * PIXEL to Vec3d(12.000, 32.000, -2.000) * PIXEL,
        Vec3d(0.000, 8.000, -30.000) * PIXEL to Vec3d(12.000, 32.000, -18.000) * PIXEL,
        Vec3d(36.000, 8.000, -30.000) * PIXEL to Vec3d(48.000, 32.000, -18.000) * PIXEL,
        Vec3d(36.000, 8.000, -14.000) * PIXEL to Vec3d(48.000, 32.000, -2.000) * PIXEL,
        Vec3d(14.000, 8.000, -32.000) * PIXEL to Vec3d(34.000, 36.000, 4.000) * PIXEL,
        Vec3d(3.000, 3.000, -48.000) * PIXEL to Vec3d(45.000, 45.000, -40.000) * PIXEL,
        Vec3d(6.000, 6.000, -40.000) * PIXEL to Vec3d(42.000, 42.000, -32.000) * PIXEL,
        Vec3d(2.000, 32.000, -12.000) * PIXEL to Vec3d(10.000, 34.000, -4.000) * PIXEL,
        Vec3d(2.000, 32.000, -28.000) * PIXEL to Vec3d(10.000, 34.000, -20.000) * PIXEL,
        Vec3d(21.000, 36.000, -11.000) * PIXEL to Vec3d(27.000, 48.000, -5.000) * PIXEL,
        Vec3d(0.000, 44.000, -48.000) * PIXEL to Vec3d(4.000, 48.000, 16.000) * PIXEL,
        Vec3d(44.000, 44.000, -48.000) * PIXEL to Vec3d(48.000, 48.000, 16.000) * PIXEL,
        Vec3d(17.000, 17.000, 13.000) * PIXEL to Vec3d(31.000, 31.000, 16.000) * PIXEL,
        Vec3d(0.000, 8.000, 12.000) * PIXEL to Vec3d(4.000, 44.000, 16.000) * PIXEL,
        Vec3d(44.000, 8.000, 12.000) * PIXEL to Vec3d(48.000, 44.000, 16.000) * PIXEL,
        Vec3d(4.000, 44.000, 12.000) * PIXEL to Vec3d(44.000, 48.000, 16.000) * PIXEL,
        Vec3d(0.000, 0.000, -48.000) * PIXEL to Vec3d(4.000, 4.000, -37.000) * PIXEL,
        Vec3d(44.000, 0.000, -48.000) * PIXEL to Vec3d(48.000, 4.000, -37.000) * PIXEL,
        Vec3d(1.000, 37.000, -30.000) * PIXEL to Vec3d(20.000, 41.000, -26.000) * PIXEL,
        Vec3d(1.000, 37.000, -37.000) * PIXEL to Vec3d(5.000, 41.000, -30.000) * PIXEL,
        Vec3d(1.000, 21.000, -37.000) * PIXEL to Vec3d(5.000, 37.000, -33.000) * PIXEL,
        Vec3d(5.000, 21.000, -37.000) * PIXEL to Vec3d(6.000, 25.000, -33.000) * PIXEL,
        Vec3d(28.000, 37.000, -26.000) * PIXEL to Vec3d(32.000, 41.000, -20.000) * PIXEL,
        Vec3d(43.000, 37.000, -37.000) * PIXEL to Vec3d(47.000, 41.000, -30.000) * PIXEL,
        Vec3d(43.000, 21.000, -37.000) * PIXEL to Vec3d(47.000, 37.000, -33.000) * PIXEL,
        Vec3d(42.000, 21.000, -37.000) * PIXEL to Vec3d(43.000, 25.000, -33.000) * PIXEL,
        Vec3d(28.000, 37.000, -30.000) * PIXEL to Vec3d(47.000, 41.000, -26.000) * PIXEL,
        Vec3d(16.000, 36.000, -24.000) * PIXEL to Vec3d(20.000, 37.000, -20.000) * PIXEL,
        Vec3d(16.000, 37.000, -26.000) * PIXEL to Vec3d(20.000, 41.000, -20.000) * PIXEL,
        Vec3d(28.000, 36.000, -24.000) * PIXEL to Vec3d(32.000, 37.000, -20.000) * PIXEL,
        Vec3d(16.000, 36.000, -15.000) * PIXEL to Vec3d(20.000, 37.000, -11.000) * PIXEL,
        Vec3d(28.000, 36.000, -15.000) * PIXEL to Vec3d(32.000, 37.000, -11.000) * PIXEL,
        Vec3d(16.000, 37.000, -15.000) * PIXEL to Vec3d(20.000, 41.000, -6.000) * PIXEL,
        Vec3d(28.000, 37.000, -15.000) * PIXEL to Vec3d(32.000, 41.000, -6.000) * PIXEL,
        Vec3d(20.000, 37.000, -10.000) * PIXEL to Vec3d(21.000, 41.000, -6.000) * PIXEL,
        Vec3d(27.000, 37.000, -10.000) * PIXEL to Vec3d(28.000, 41.000, -6.000) * PIXEL,
        Vec3d(22.000, 37.000, -5.000) * PIXEL to Vec3d(26.000, 41.000, -4.000) * PIXEL,
        Vec3d(22.000, 36.000, -4.000) * PIXEL to Vec3d(26.000, 41.000, 0.000) * PIXEL,
        Vec3d(38.000, 32.000, -12.000) * PIXEL to Vec3d(46.000, 34.000, -4.000) * PIXEL,
        Vec3d(38.000, 32.000, -28.000) * PIXEL to Vec3d(46.000, 34.000, -20.000) * PIXEL,
        Vec3d(22.000, 22.000, 11.000) * PIXEL to Vec3d(26.000, 26.000, 13.000) * PIXEL,
        Vec3d(16.000, 8.000, 4.000) * PIXEL to Vec3d(32.000, 32.000, 6.000) * PIXEL,
        Vec3d(22.000, 8.000, 7.000) * PIXEL to Vec3d(26.000, 26.000, 11.000) * PIXEL,
        Vec3d(2.000, 2.000, -64.000) * PIXEL to Vec3d(46.000, 46.000, -63.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 0) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}