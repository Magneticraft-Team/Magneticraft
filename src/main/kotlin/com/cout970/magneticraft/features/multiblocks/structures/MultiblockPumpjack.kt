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

object MultiblockPumpjack : Multiblock() {

    override val name: String = "pumpjack"
    override val size: BlockPos = BlockPos(3, 5, 6)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val I = IgnoreBlockComponent
        val V = columnBlock(EnumFacing.UP)
        val H = columnBlock(EnumFacing.NORTH)
        val C = copperCoilBlock()
        val G = grateBlock()
        val B = baseBlock()
        val R = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(

            zLayers(listOf(I, I, I), // y = 4
                listOf(G, G, G),
                listOf(G, G, G),
                listOf(G, G, G),
                listOf(I, G, I),
                listOf(I, R, I)),

            zLayers(listOf(I, I, I), // y = 3
                listOf(G, H, G),
                listOf(G, H, G),
                listOf(G, H, G),
                listOf(I, H, I),
                listOf(I, R, I)),

            zLayers(listOf(I, I, I), // y = 2
                listOf(G, R, G),
                listOf(G, G, G),
                listOf(G, V, G),
                listOf(I, G, I),
                listOf(I, R, I)),

            zLayers(listOf(I, I, I), // y = 1
                listOf(G, R, G),
                listOf(G, G, G),
                listOf(G, V, G),
                listOf(I, G, I),
                listOf(I, G, I)),

            zLayers(listOf(B, M, C), // y = 0
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B))
        )
    }

    override fun getControllerBlock() = Multiblocks.pumpjack

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(-16.000, 0.000, -16.000) * PIXEL to Vec3d(80.000, 8.000, 32.000) * PIXEL,
        Vec3d(38.000, 9.000, -7.250) * PIXEL to Vec3d(45.250, 48.000, 6.000) * PIXEL,
        Vec3d(38.000, 9.000, 10.000) * PIXEL to Vec3d(45.250, 48.000, 23.250) * PIXEL,
        Vec3d(27.750, 9.000, 10.000) * PIXEL to Vec3d(35.000, 48.000, 23.250) * PIXEL,
        Vec3d(27.750, 9.000, -7.250) * PIXEL to Vec3d(35.000, 48.000, 6.000) * PIXEL,
        Vec3d(33.000, 48.000, 4.000) * PIXEL to Vec3d(40.000, 50.000, 12.000) * PIXEL,
        Vec3d(34.500, 50.000, 4.000) * PIXEL to Vec3d(38.500, 54.000, 12.000) * PIXEL,
        Vec3d(13.000, 55.000, 6.000) * PIXEL to Vec3d(68.000, 61.000, 10.000) * PIXEL,
        Vec3d(68.000, 39.000, 4.000) * PIXEL to Vec3d(77.000, 68.000, 12.000) * PIXEL,
        Vec3d(68.500, 8.000, 4.500) * PIXEL to Vec3d(75.500, 14.000, 11.500) * PIXEL,
        Vec3d(13.000, 61.000, 5.000) * PIXEL to Vec3d(68.000, 62.000, 11.000) * PIXEL,
        Vec3d(13.000, 54.000, 5.000) * PIXEL to Vec3d(68.000, 55.000, 11.000) * PIXEL,
        Vec3d(66.000, 51.000, 4.500) * PIXEL to Vec3d(68.000, 64.000, 11.500) * PIXEL,
        Vec3d(5.000, 53.000, 4.000) * PIXEL to Vec3d(13.000, 63.000, 12.000) * PIXEL,
        Vec3d(7.000, 49.000, 6.000) * PIXEL to Vec3d(11.000, 53.000, 10.000) * PIXEL,
        Vec3d(7.000, 46.000, -2.000) * PIXEL to Vec3d(11.000, 49.000, 18.000) * PIXEL,
        Vec3d(1.000, 8.000, 3.000) * PIXEL to Vec3d(17.000, 19.000, 13.000) * PIXEL,
        Vec3d(7.000, 20.000, -1.000) * PIXEL to Vec3d(11.000, 24.000, 17.000) * PIXEL,
        Vec3d(6.500, 9.000, -2.000) * PIXEL to Vec3d(11.500, 26.000, 0.000) * PIXEL,
        Vec3d(6.500, 9.000, 16.000) * PIXEL to Vec3d(11.500, 26.000, 18.000) * PIXEL,
        Vec3d(1.000, 10.000, -0.500) * PIXEL to Vec3d(17.000, 17.000, 1.500) * PIXEL,
        Vec3d(7.250, 18.000, -4.000) * PIXEL to Vec3d(11.250, 49.000, -2.000) * PIXEL,
        Vec3d(-16.000, 8.000, 0.000) * PIXEL to Vec3d(-8.000, 16.000, 16.000) * PIXEL,
        Vec3d(-13.000, 8.000, 16.000) * PIXEL to Vec3d(-11.000, 10.000, 26.000) * PIXEL,
        Vec3d(-13.000, 8.000, 26.000) * PIXEL to Vec3d(53.000, 10.000, 28.000) * PIXEL,
        Vec3d(51.000, 8.000, 7.000) * PIXEL to Vec3d(53.000, 10.000, 26.000) * PIXEL,
        Vec3d(50.500, 8.000, 6.500) * PIXEL to Vec3d(53.500, 11.000, 9.500) * PIXEL,
        Vec3d(17.000, 8.000, 3.000) * PIXEL to Vec3d(51.000, 10.000, 5.000) * PIXEL,
        Vec3d(51.000, 8.000, 3.000) * PIXEL to Vec3d(53.000, 10.000, 7.000) * PIXEL,
        Vec3d(69.500, 14.000, 5.500) * PIXEL to Vec3d(74.500, 16.000, 10.500) * PIXEL,
        Vec3d(69.314, 14.981, 7.000) * PIXEL to Vec3d(73.460, 37.072, 9.000) * PIXEL,
        Vec3d(7.250, 18.000, 18.000) * PIXEL to Vec3d(11.250, 49.000, 20.000) * PIXEL,
        Vec3d(31.000, 19.678, -4.172) * PIXEL to Vec3d(42.000, 22.139, -1.711) * PIXEL,
        Vec3d(-13.000, 8.000, -13.000) * PIXEL to Vec3d(-3.000, 15.000, -3.000) * PIXEL,
        Vec3d(30.000, 10.678, -6.672) * PIXEL to Vec3d(43.000, 13.139, -4.211) * PIXEL,
        Vec3d(32.500, 29.678, -1.172) * PIXEL to Vec3d(40.500, 32.139, 1.289) * PIXEL,
        Vec3d(33.500, 38.678, 1.516) * PIXEL to Vec3d(39.500, 41.139, 3.977) * PIXEL,
        Vec3d(33.500, 38.705, 12.116) * PIXEL to Vec3d(39.500, 41.199, 14.611) * PIXEL,
        Vec3d(32.500, 29.663, 14.661) * PIXEL to Vec3d(40.500, 32.158, 17.155) * PIXEL,
        Vec3d(31.000, 19.610, 17.476) * PIXEL to Vec3d(42.000, 22.104, 19.971) * PIXEL,
        Vec3d(30.000, 10.669, 20.179) * PIXEL to Vec3d(43.000, 13.163, 22.674) * PIXEL,
        Vec3d(2.500, 8.000, 2.000) * PIXEL to Vec3d(3.500, 19.000, 3.000) * PIXEL,
        Vec3d(1.000, 19.000, 2.000) * PIXEL to Vec3d(17.000, 20.000, 14.000) * PIXEL,
        Vec3d(1.000, 20.000, 2.500) * PIXEL to Vec3d(17.000, 26.000, 13.500) * PIXEL,
        Vec3d(5.500, 26.000, 2.500) * PIXEL to Vec3d(12.500, 28.000, 13.500) * PIXEL,
        Vec3d(0.000, 12.000, -0.500) * PIXEL to Vec3d(1.000, 17.000, 1.500) * PIXEL,
        Vec3d(17.000, 12.000, -0.500) * PIXEL to Vec3d(18.000, 17.000, 1.500) * PIXEL,
        Vec3d(-1.000, 14.000, -0.500) * PIXEL to Vec3d(0.000, 17.000, 1.500) * PIXEL,
        Vec3d(18.000, 14.000, -0.500) * PIXEL to Vec3d(19.000, 17.000, 1.500) * PIXEL,
        Vec3d(18.000, 14.000, 14.500) * PIXEL to Vec3d(19.000, 17.000, 16.500) * PIXEL,
        Vec3d(17.000, 12.000, 14.500) * PIXEL to Vec3d(18.000, 17.000, 16.500) * PIXEL,
        Vec3d(1.000, 10.000, 14.500) * PIXEL to Vec3d(17.000, 17.000, 16.500) * PIXEL,
        Vec3d(0.000, 12.000, 14.500) * PIXEL to Vec3d(1.000, 17.000, 16.500) * PIXEL,
        Vec3d(-1.000, 14.000, 14.500) * PIXEL to Vec3d(0.000, 17.000, 16.500) * PIXEL,
        Vec3d(68.000, 71.000, 4.000) * PIXEL to Vec3d(73.000, 74.000, 12.000) * PIXEL,
        Vec3d(68.000, 33.000, 4.000) * PIXEL to Vec3d(73.000, 36.000, 12.000) * PIXEL,
        Vec3d(68.000, 36.000, 4.000) * PIXEL to Vec3d(75.000, 39.000, 12.000) * PIXEL,
        Vec3d(32.500, 55.000, 10.000) * PIXEL to Vec3d(33.500, 61.000, 11.000) * PIXEL,
        Vec3d(39.500, 55.000, 10.000) * PIXEL to Vec3d(40.500, 61.000, 11.000) * PIXEL,
        Vec3d(39.500, 55.000, 5.000) * PIXEL to Vec3d(40.500, 61.000, 6.000) * PIXEL,
        Vec3d(32.500, 55.000, 5.000) * PIXEL to Vec3d(33.500, 61.000, 6.000) * PIXEL,
        Vec3d(-10.000, 15.000, -10.000) * PIXEL to Vec3d(-6.000, 16.000, -6.000) * PIXEL,
        Vec3d(14.000, 8.000, -13.000) * PIXEL to Vec3d(24.000, 10.000, -6.000) * PIXEL,
        Vec3d(-6.000, 8.000, -3.000) * PIXEL to Vec3d(-5.000, 9.000, 7.000) * PIXEL,
        Vec3d(-6.000, 8.000, 7.000) * PIXEL to Vec3d(1.000, 9.000, 8.000) * PIXEL,
        Vec3d(1.000, 8.000, -13.000) * PIXEL to Vec3d(11.000, 10.000, -6.000) * PIXEL,
        Vec3d(-4.000, 8.000, -8.000) * PIXEL to Vec3d(1.000, 9.000, -7.000) * PIXEL,
        Vec3d(-4.000, 8.000, -12.000) * PIXEL to Vec3d(1.000, 9.000, -11.000) * PIXEL,
        Vec3d(11.000, 8.000, -10.000) * PIXEL to Vec3d(14.000, 9.000, -9.000) * PIXEL,
        Vec3d(21.000, 8.000, -6.000) * PIXEL to Vec3d(22.000, 9.000, 3.000) * PIXEL,
        Vec3d(21.000, 8.000, 5.000) * PIXEL to Vec3d(22.000, 9.000, 11.000) * PIXEL,
        Vec3d(17.000, 8.000, 11.000) * PIXEL to Vec3d(22.000, 9.000, 12.000) * PIXEL,
        Vec3d(-14.000, 8.000, 24.000) * PIXEL to Vec3d(-5.000, 11.000, 30.000) * PIXEL,
        Vec3d(-13.000, 8.000, 26.000) * PIXEL to Vec3d(53.000, 10.000, 28.000) * PIXEL,
        Vec3d(68.000, 68.000, 4.000) * PIXEL to Vec3d(75.000, 71.000, 12.000) * PIXEL,
        Vec3d(53.000, 8.000, 7.000) * PIXEL to Vec3d(69.000, 10.000, 9.000) * PIXEL,
        Vec3d(5.500, 8.000, 2.000) * PIXEL to Vec3d(6.500, 19.000, 3.000) * PIXEL,
        Vec3d(8.500, 8.000, 2.000) * PIXEL to Vec3d(9.500, 19.000, 3.000) * PIXEL,
        Vec3d(11.500, 8.000, 2.000) * PIXEL to Vec3d(12.500, 19.000, 3.000) * PIXEL,
        Vec3d(14.500, 8.000, 2.000) * PIXEL to Vec3d(15.500, 19.000, 3.000) * PIXEL,
        Vec3d(2.500, 8.000, 13.000) * PIXEL to Vec3d(3.500, 19.000, 14.000) * PIXEL,
        Vec3d(5.500, 8.000, 13.000) * PIXEL to Vec3d(6.500, 19.000, 14.000) * PIXEL,
        Vec3d(8.500, 8.000, 13.000) * PIXEL to Vec3d(9.500, 19.000, 14.000) * PIXEL,
        Vec3d(11.500, 8.000, 13.000) * PIXEL to Vec3d(12.500, 19.000, 14.000) * PIXEL,
        Vec3d(14.500, 8.000, 13.000) * PIXEL to Vec3d(15.500, 19.000, 14.000) * PIXEL
    ).map { EnumFacing.EAST.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}