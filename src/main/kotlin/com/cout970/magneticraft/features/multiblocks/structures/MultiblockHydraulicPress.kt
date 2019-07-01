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

object MultiblockHydraulicPress : Multiblock() {

    override val name: String = "hydraulic_press"
    override val size: BlockPos = BlockPos(3, 5, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val I = IgnoreBlockComponent
        val R = corrugatedIronBlock()
        val G = grateBlock()
        val C = copperCoilBlock()
        val Y = strippedBlock()
        val U = columnBlock(EnumFacing.UP)
        val H = columnBlock(EnumFacing.EAST)
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(

            zLayers(
                listOf(I, I, I), // y = 4
                listOf(H, H, H),
                listOf(I, I, I)),

            zLayers(
                listOf(I, I, I), // y = 3
                listOf(U, R, U),
                listOf(I, I, I)),

            zLayers(
                listOf(I, I, I), // y = 2
                listOf(U, Y, U),
                listOf(I, I, I)),

            zLayers(
                listOf(G, G, G), // y = 1
                listOf(C, R, C),
                listOf(G, G, G)),

            zLayers(
                listOf(G, M, G), // y = 0
                listOf(G, G, G),
                listOf(G, G, G))
        )
    }

    override fun getControllerBlock() = Multiblocks.hydraulicPress

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(-14.000, 4.000, 1.000) * PIXEL to Vec3d(-3.000, 20.000, 15.000) * PIXEL,
        Vec3d(2.000, 16.000, 2.000) * PIXEL to Vec3d(14.000, 30.000, 14.000) * PIXEL,
        Vec3d(2.000, 40.000, 13.000) * PIXEL to Vec3d(14.000, 58.000, 14.000) * PIXEL,
        Vec3d(0.000, 40.000, 12.000) * PIXEL to Vec3d(3.000, 58.000, 13.000) * PIXEL,
        Vec3d(0.000, 40.000, 3.000) * PIXEL to Vec3d(3.000, 58.000, 4.000) * PIXEL,
        Vec3d(2.000, 40.000, 2.000) * PIXEL to Vec3d(14.000, 58.000, 3.000) * PIXEL,
        Vec3d(-7.000, 44.000, 12.100) * PIXEL to Vec3d(23.000, 47.000, 15.100) * PIXEL,
        Vec3d(-6.000, 43.000, 12.000) * PIXEL to Vec3d(-3.000, 48.000, 16.000) * PIXEL,
        Vec3d(-11.000, 20.000, 3.000) * PIXEL to Vec3d(-1.000, 51.000, 13.000) * PIXEL,
        Vec3d(15.000, 42.000, 4.000) * PIXEL to Vec3d(25.000, 70.000, 12.000) * PIXEL,
        Vec3d(19.000, 43.000, 12.000) * PIXEL to Vec3d(22.000, 48.000, 16.000) * PIXEL,
        Vec3d(19.000, 4.000, 1.000) * PIXEL to Vec3d(30.000, 20.000, 15.000) * PIXEL,
        Vec3d(17.000, 20.000, 3.000) * PIXEL to Vec3d(27.000, 51.000, 13.000) * PIXEL,
        Vec3d(-11.000, 70.000, 2.000) * PIXEL to Vec3d(27.000, 78.000, 14.000) * PIXEL,
        Vec3d(-9.000, 42.000, 4.000) * PIXEL to Vec3d(1.000, 70.000, 12.000) * PIXEL,
        Vec3d(-7.000, 44.000, 0.900) * PIXEL to Vec3d(23.000, 47.000, 3.900) * PIXEL,
        Vec3d(19.000, 43.000, 0.000) * PIXEL to Vec3d(22.000, 48.000, 4.000) * PIXEL,
        Vec3d(-6.000, 43.000, 0.000) * PIXEL to Vec3d(-3.000, 48.000, 4.000) * PIXEL,
        Vec3d(13.000, 40.000, 3.000) * PIXEL to Vec3d(16.000, 58.000, 4.000) * PIXEL,
        Vec3d(13.000, 40.000, 12.000) * PIXEL to Vec3d(16.000, 58.000, 13.000) * PIXEL,
        Vec3d(-2.000, 0.000, 3.000) * PIXEL to Vec3d(-1.000, 1.000, 13.000) * PIXEL,
        Vec3d(17.000, 0.000, 3.000) * PIXEL to Vec3d(18.000, 1.000, 13.000) * PIXEL,
        Vec3d(27.000, 18.000, 4.000) * PIXEL to Vec3d(31.000, 30.000, 12.000) * PIXEL,
        Vec3d(31.000, 22.000, 6.000) * PIXEL to Vec3d(32.000, 26.000, 10.000) * PIXEL,
        Vec3d(3.000, 38.000, 3.000) * PIXEL to Vec3d(13.000, 52.000, 13.000) * PIXEL,
        Vec3d(6.500, 52.000, 6.500) * PIXEL to Vec3d(9.500, 80.000, 9.500) * PIXEL,
        Vec3d(-16.000, 0.000, -16.000) * PIXEL to Vec3d(0.000, 4.000, 32.000) * PIXEL,
        Vec3d(16.000, 0.000, -16.000) * PIXEL to Vec3d(32.000, 4.000, 32.000) * PIXEL,
        Vec3d(0.000, 12.000, -12.000) * PIXEL to Vec3d(16.000, 20.000, -4.000) * PIXEL,
        Vec3d(1.000, 11.460, -15.654) * PIXEL to Vec3d(15.000, 14.000, -11.593) * PIXEL,
        Vec3d(0.000, 0.000, -16.000) * PIXEL to Vec3d(1.000, 20.000, -12.000) * PIXEL,
        Vec3d(15.000, 0.000, -16.000) * PIXEL to Vec3d(16.000, 20.000, -12.000) * PIXEL,
        Vec3d(1.000, 19.000, -16.000) * PIXEL to Vec3d(15.000, 20.000, -12.000) * PIXEL,
        Vec3d(-10.000, 12.000, -11.000) * PIXEL to Vec3d(8.000, 24.000, 27.000) * PIXEL,
        Vec3d(8.000, 12.000, -11.000) * PIXEL to Vec3d(26.000, 24.000, 27.000) * PIXEL,
        Vec3d(-12.000, 0.000, -12.000) * PIXEL to Vec3d(8.000, 12.000, 8.000) * PIXEL,
        Vec3d(8.000, 0.000, -12.000) * PIXEL to Vec3d(28.000, 12.000, 8.000) * PIXEL,
        Vec3d(8.000, 0.000, 8.000) * PIXEL to Vec3d(28.000, 12.000, 28.000) * PIXEL,
        Vec3d(-12.000, 0.000, 8.000) * PIXEL to Vec3d(8.000, 12.000, 28.000) * PIXEL,
        Vec3d(-14.500, 18.000, 4.000) * PIXEL to Vec3d(-10.500, 30.000, 12.000) * PIXEL,
        Vec3d(-15.500, 22.000, 6.000) * PIXEL to Vec3d(-14.500, 26.000, 10.000) * PIXEL,
        Vec3d(0.000, 0.000, 24.000) * PIXEL to Vec3d(16.000, 15.000, 32.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}