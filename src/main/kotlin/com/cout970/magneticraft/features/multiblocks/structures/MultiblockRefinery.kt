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

object MultiblockRefinery : Multiblock() {

    override val name: String = "refinery"
    override val size: BlockPos = BlockPos(3, 9, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val U = columnBlock(EnumFacing.UP)
        val H = columnBlock(EnumFacing.NORTH)
        val I = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(U, U, U), // y = 8
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 7
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 6
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 5
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 4
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 3
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 2
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, U, U), // y = 1
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(U, M, U), // y = 0
                listOf(H, H, H),
                listOf(H, H, H))
        )
    }

    override fun getControllerBlock() = Multiblocks.refinery

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(-13.000, 34.000, -13.000) * PIXEL to Vec3d(29.000, 46.000, 29.000) * PIXEL,
        Vec3d(-14.000, 46.000, -14.000) * PIXEL to Vec3d(30.000, 50.000, 30.000) * PIXEL,
        Vec3d(-14.000, 62.000, -14.000) * PIXEL to Vec3d(30.000, 66.000, 30.000) * PIXEL,
        Vec3d(-14.000, 78.000, -14.000) * PIXEL to Vec3d(30.000, 82.000, 30.000) * PIXEL,
        Vec3d(-14.000, 94.000, -14.000) * PIXEL to Vec3d(30.000, 98.000, 30.000) * PIXEL,
        Vec3d(-14.000, 110.000, -14.000) * PIXEL to Vec3d(30.000, 114.000, 30.000) * PIXEL,
        Vec3d(-16.000, 10.000, -16.000) * PIXEL to Vec3d(32.000, 16.000, 32.000) * PIXEL,
        Vec3d(29.000, 51.000, 3.000) * PIXEL to Vec3d(32.000, 61.000, 13.000) * PIXEL,
        Vec3d(29.000, 83.000, 3.000) * PIXEL to Vec3d(32.000, 93.000, 13.000) * PIXEL,
        Vec3d(29.000, 115.000, 3.000) * PIXEL to Vec3d(32.000, 125.000, 13.000) * PIXEL,
        Vec3d(-16.000, 51.000, 4.000) * PIXEL to Vec3d(-13.000, 61.000, 14.000) * PIXEL,
        Vec3d(-16.000, 83.000, 4.000) * PIXEL to Vec3d(-13.000, 93.000, 14.000) * PIXEL,
        Vec3d(-16.000, 115.000, 4.000) * PIXEL to Vec3d(-13.000, 125.000, 14.000) * PIXEL,
        Vec3d(-14.000, 142.000, -14.000) * PIXEL to Vec3d(30.000, 144.000, 30.000) * PIXEL,
        Vec3d(3.000, 51.000, -16.000) * PIXEL to Vec3d(13.000, 61.000, -13.000) * PIXEL,
        Vec3d(3.000, 83.000, -16.000) * PIXEL to Vec3d(13.000, 93.000, -13.000) * PIXEL,
        Vec3d(3.000, 115.000, -16.000) * PIXEL to Vec3d(13.000, 125.000, -13.000) * PIXEL,
        Vec3d(3.000, 115.000, 29.000) * PIXEL to Vec3d(13.000, 125.000, 32.000) * PIXEL,
        Vec3d(3.000, 83.000, 29.000) * PIXEL to Vec3d(13.000, 93.000, 32.000) * PIXEL,
        Vec3d(3.000, 51.000, 29.000) * PIXEL to Vec3d(13.000, 61.000, 32.000) * PIXEL,
        Vec3d(3.000, 19.000, -17.000) * PIXEL to Vec3d(13.000, 29.000, -14.000) * PIXEL,
        Vec3d(30.000, 22.000, 6.000) * PIXEL to Vec3d(32.000, 26.000, 10.000) * PIXEL,
        Vec3d(-16.000, 22.000, 6.000) * PIXEL to Vec3d(-14.000, 26.000, 10.000) * PIXEL,
        Vec3d(2.000, 19.000, 30.000) * PIXEL to Vec3d(14.000, 27.000, 31.000) * PIXEL,
        Vec3d(-14.000, 16.000, -14.000) * PIXEL to Vec3d(30.000, 30.000, 30.000) * PIXEL,
        Vec3d(-14.000, 0.000, -14.000) * PIXEL to Vec3d(30.000, 10.000, 30.000) * PIXEL,
        Vec3d(-16.000, 30.000, -16.000) * PIXEL to Vec3d(32.000, 34.000, 32.000) * PIXEL,
        Vec3d(-13.000, 50.000, -13.000) * PIXEL to Vec3d(29.000, 62.000, 29.000) * PIXEL,
        Vec3d(-13.000, 66.000, -13.000) * PIXEL to Vec3d(29.000, 78.000, 29.000) * PIXEL,
        Vec3d(-13.000, 82.000, -13.000) * PIXEL to Vec3d(29.000, 94.000, 29.000) * PIXEL,
        Vec3d(-13.000, 98.000, -13.000) * PIXEL to Vec3d(29.000, 110.000, 29.000) * PIXEL,
        Vec3d(-13.000, 114.000, -13.000) * PIXEL to Vec3d(29.000, 126.000, 29.000) * PIXEL,
        Vec3d(-13.000, 130.000, -13.000) * PIXEL to Vec3d(29.000, 142.000, 29.000) * PIXEL,
        Vec3d(-14.000, 126.000, -14.000) * PIXEL to Vec3d(30.000, 130.000, 30.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}