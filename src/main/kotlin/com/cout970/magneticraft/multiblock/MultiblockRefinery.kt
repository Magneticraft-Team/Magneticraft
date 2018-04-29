package com.cout970.magneticraft.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.multiblock.core.*
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.vec3Of
import com.cout970.vector.extensions.times
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

object MultiblockRefinery : Multiblock() {

    override val name: String = "refinery"
    override val size: BlockPos = BlockPos(3, 8, 3)
    override val scheme: List<Multiblock.MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val U = columnBlock(EnumFacing.UP)
        val H = columnBlock(EnumFacing.NORTH)
        val I = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = Multiblock.yLayers(
                Multiblock.zLayers(
                        listOf(U, U, U), // y = 7
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, U, U), // y = 6
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, U, U), // y = 5
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, U, U), // y = 4
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, U, U), // y = 3
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, U, U), // y = 2
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, U, U), // y = 1
                        listOf(I, I, I),
                        listOf(I, I, I)),

                Multiblock.zLayers(
                        listOf(U, M, U), // y = 0
                        listOf(H, H, H),
                        listOf(H, H, H))
        )
    }

    override fun getControllerBlock() = Multiblocks.refinery

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = listOf(
            Vec3d(0.000, 0.000, -32.000) * PIXEL to Vec3d(48.000, 16.000, 16.000) * PIXEL,
            Vec3d(31.000, 16.000, -31.000) * PIXEL to Vec3d(47.000, 32.000, -17.000) * PIXEL,
            Vec3d(18.000, 16.000, -30.000) * PIXEL to Vec3d(30.000, 32.000, -18.000) * PIXEL,
            Vec3d(1.000, 16.000, -31.000) * PIXEL to Vec3d(17.000, 32.000, -17.000) * PIXEL,
            Vec3d(1.000, 32.000, -31.000) * PIXEL to Vec3d(47.000, 128.000, -17.000) * PIXEL,
            Vec3d(3.000, 32.000, -17.000) * PIXEL to Vec3d(24.000, 126.000, 13.000) * PIXEL,
            Vec3d(2.000, 121.000, -17.000) * PIXEL to Vec3d(3.000, 122.000, 13.000) * PIXEL,
            Vec3d(2.000, 112.000, -17.000) * PIXEL to Vec3d(3.000, 113.000, 13.000) * PIXEL,
            Vec3d(2.000, 103.000, -17.000) * PIXEL to Vec3d(3.000, 104.000, 13.000) * PIXEL,
            Vec3d(2.000, 94.000, -17.000) * PIXEL to Vec3d(3.000, 95.000, 13.000) * PIXEL,
            Vec3d(2.000, 85.000, -17.000) * PIXEL to Vec3d(3.000, 86.000, 13.000) * PIXEL,
            Vec3d(2.000, 76.000, -17.000) * PIXEL to Vec3d(3.000, 77.000, 13.000) * PIXEL,
            Vec3d(2.000, 67.000, -17.000) * PIXEL to Vec3d(3.000, 68.000, 13.000) * PIXEL,
            Vec3d(2.000, 58.000, -17.000) * PIXEL to Vec3d(3.000, 59.000, 13.000) * PIXEL,
            Vec3d(2.000, 49.000, -17.000) * PIXEL to Vec3d(3.000, 50.000, 13.000) * PIXEL,
            Vec3d(2.000, 121.000, 13.000) * PIXEL to Vec3d(46.000, 122.000, 14.000) * PIXEL,
            Vec3d(2.000, 112.000, 13.000) * PIXEL to Vec3d(46.000, 113.000, 14.000) * PIXEL,
            Vec3d(2.000, 103.000, 13.000) * PIXEL to Vec3d(46.000, 104.000, 14.000) * PIXEL,
            Vec3d(2.000, 94.000, 13.000) * PIXEL to Vec3d(46.000, 95.000, 14.000) * PIXEL,
            Vec3d(2.000, 85.000, 13.000) * PIXEL to Vec3d(46.000, 86.000, 14.000) * PIXEL,
            Vec3d(2.000, 76.000, 13.000) * PIXEL to Vec3d(46.000, 77.000, 14.000) * PIXEL,
            Vec3d(2.000, 67.000, 13.000) * PIXEL to Vec3d(46.000, 68.000, 14.000) * PIXEL,
            Vec3d(2.000, 58.000, 13.000) * PIXEL to Vec3d(46.000, 59.000, 14.000) * PIXEL,
            Vec3d(2.000, 49.000, 13.000) * PIXEL to Vec3d(46.000, 50.000, 14.000) * PIXEL,
            Vec3d(1.000, 30.000, -17.000) * PIXEL to Vec3d(47.000, 32.000, 15.000) * PIXEL,
            Vec3d(2.000, 16.000, -17.000) * PIXEL to Vec3d(46.000, 30.000, 13.000) * PIXEL,
            Vec3d(23.000, 22.000, 13.000) * PIXEL to Vec3d(25.000, 24.000, 16.000) * PIXEL,
            Vec3d(20.588, 22.928, 14.000) * PIXEL to Vec3d(24.048, 26.535, 15.000) * PIXEL,
            Vec3d(23.952, 22.928, 14.000) * PIXEL to Vec3d(27.412, 26.535, 15.000) * PIXEL,
            Vec3d(20.556, 19.495, 14.000) * PIXEL to Vec3d(24.054, 23.067, 15.000) * PIXEL,
            Vec3d(23.946, 19.495, 14.000) * PIXEL to Vec3d(27.444, 23.067, 15.000) * PIXEL,
            Vec3d(20.000, 26.000, 14.000) * PIXEL to Vec3d(28.000, 27.000, 15.000) * PIXEL,
            Vec3d(20.000, 19.000, 14.000) * PIXEL to Vec3d(28.000, 20.000, 15.000) * PIXEL,
            Vec3d(20.000, 20.000, 14.000) * PIXEL to Vec3d(21.000, 26.000, 15.000) * PIXEL,
            Vec3d(27.000, 20.000, 14.000) * PIXEL to Vec3d(28.000, 26.000, 15.000) * PIXEL,
            Vec3d(2.000, 40.000, -17.000) * PIXEL to Vec3d(3.000, 41.000, 13.000) * PIXEL,
            Vec3d(2.000, 40.000, 13.000) * PIXEL to Vec3d(46.000, 41.000, 14.000) * PIXEL,
            Vec3d(45.000, 121.000, -17.000) * PIXEL to Vec3d(46.000, 122.000, 13.000) * PIXEL,
            Vec3d(45.000, 112.000, -17.000) * PIXEL to Vec3d(46.000, 113.000, 13.000) * PIXEL,
            Vec3d(45.000, 103.000, -17.000) * PIXEL to Vec3d(46.000, 104.000, 13.000) * PIXEL,
            Vec3d(45.000, 94.000, -17.000) * PIXEL to Vec3d(46.000, 95.000, 13.000) * PIXEL,
            Vec3d(45.000, 85.000, -17.000) * PIXEL to Vec3d(46.000, 86.000, 13.000) * PIXEL,
            Vec3d(45.000, 76.000, -17.000) * PIXEL to Vec3d(46.000, 77.000, 13.000) * PIXEL,
            Vec3d(45.000, 67.000, -17.000) * PIXEL to Vec3d(46.000, 68.000, 13.000) * PIXEL,
            Vec3d(45.000, 58.000, -17.000) * PIXEL to Vec3d(46.000, 59.000, 13.000) * PIXEL,
            Vec3d(45.000, 49.000, -17.000) * PIXEL to Vec3d(46.000, 50.000, 13.000) * PIXEL,
            Vec3d(45.000, 40.000, -17.000) * PIXEL to Vec3d(46.000, 41.000, 13.000) * PIXEL,
            Vec3d(20.000, 52.000, -32.000) * PIXEL to Vec3d(28.000, 60.000, -31.000) * PIXEL,
            Vec3d(20.000, 84.000, -32.000) * PIXEL to Vec3d(28.000, 92.000, -31.000) * PIXEL,
            Vec3d(20.000, 116.000, -32.000) * PIXEL to Vec3d(28.000, 124.000, -31.000) * PIXEL,
            Vec3d(20.000, 20.000, -32.000) * PIXEL to Vec3d(28.000, 28.000, -30.000) * PIXEL,
            Vec3d(24.000, 32.000, -17.000) * PIXEL to Vec3d(45.000, 126.000, 13.000) * PIXEL
    ).map { it + vec3Of(1, 0, 0) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}