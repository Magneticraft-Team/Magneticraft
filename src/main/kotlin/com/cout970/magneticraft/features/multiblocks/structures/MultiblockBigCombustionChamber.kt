package com.cout970.magneticraft.features.multiblocks.structures

import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.rotateBox
import com.cout970.magneticraft.misc.vector.times
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.multiblocks.*
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks


object MultiblockBigCombustionChamber : Multiblock() {

    override val name: String = "big_combustion_chamber"
    override val size: BlockPos = BlockPos(3, 2, 4)
    override val scheme: List<Multiblock.MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val A = IgnoreBlockComponent
        val C = corrugatedIronBlock()
        val B = ofBlock(Blocks.BRICK_BLOCK)
        val M = mainBlockOf(controllerBlock)

        scheme = Multiblock.yLayers(
            Multiblock.zLayers(
                listOf(B, B, B), // y = 1
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(A, C, A)),

            Multiblock.zLayers(
                listOf(B, M, B), // y = 0
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(A, C, A))
        )
    }

    override fun getControllerBlock() = Multiblocks.bigCombustionChamber

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(-16.000, 30.000, -16.000) * PIXEL to Vec3d(32.000, 32.000, 32.000) * PIXEL,
        Vec3d(24.000, 2.000, 24.000) * PIXEL to Vec3d(32.000, 30.000, 32.000) * PIXEL,
        Vec3d(-16.000, 3.000, 3.000) * PIXEL to Vec3d(-15.000, 13.000, 13.000) * PIXEL,
        Vec3d(12.000, 2.000, 29.000) * PIXEL to Vec3d(14.000, 12.000, 31.000) * PIXEL,
        Vec3d(-8.000, 14.000, 29.000) * PIXEL to Vec3d(24.000, 16.000, 32.000) * PIXEL,
        Vec3d(-8.000, 12.000, 29.000) * PIXEL to Vec3d(-5.000, 13.000, 31.000) * PIXEL,
        Vec3d(-8.000, 13.000, 29.000) * PIXEL to Vec3d(-4.000, 14.000, 31.000) * PIXEL,
        Vec3d(1.000, 12.000, 29.000) * PIXEL to Vec3d(5.000, 13.000, 31.000) * PIXEL,
        Vec3d(0.000, 13.000, 29.000) * PIXEL to Vec3d(6.000, 14.000, 31.000) * PIXEL,
        Vec3d(11.000, 12.000, 29.000) * PIXEL to Vec3d(15.000, 13.000, 31.000) * PIXEL,
        Vec3d(10.000, 13.000, 29.000) * PIXEL to Vec3d(16.000, 14.000, 31.000) * PIXEL,
        Vec3d(20.000, 13.000, 29.000) * PIXEL to Vec3d(24.000, 14.000, 31.000) * PIXEL,
        Vec3d(21.000, 12.000, 29.000) * PIXEL to Vec3d(24.000, 13.000, 31.000) * PIXEL,
        Vec3d(-8.000, 1.000, 29.000) * PIXEL to Vec3d(24.000, 16.000, 29.000) * PIXEL,
        Vec3d(24.000, 2.000, -16.000) * PIXEL to Vec3d(32.000, 30.000, -8.000) * PIXEL,
        Vec3d(-16.000, 2.000, 24.000) * PIXEL to Vec3d(-8.000, 30.000, 32.000) * PIXEL,
        Vec3d(-16.000, 2.000, -16.000) * PIXEL to Vec3d(-8.000, 30.000, -8.000) * PIXEL,
        Vec3d(30.000, 2.000, -8.000) * PIXEL to Vec3d(31.000, 30.000, 8.000) * PIXEL,
        Vec3d(30.000, 2.000, 8.000) * PIXEL to Vec3d(31.000, 30.000, 24.000) * PIXEL,
        Vec3d(-15.000, 2.000, -8.000) * PIXEL to Vec3d(-14.000, 30.000, 8.000) * PIXEL,
        Vec3d(-15.000, 2.000, 8.000) * PIXEL to Vec3d(-14.000, 30.000, 24.000) * PIXEL,
        Vec3d(8.000, 2.000, -15.000) * PIXEL to Vec3d(24.000, 30.000, -14.000) * PIXEL,
        Vec3d(-8.000, 2.000, -15.000) * PIXEL to Vec3d(8.000, 30.000, -14.000) * PIXEL,
        Vec3d(-8.000, 16.000, 30.000) * PIXEL to Vec3d(8.000, 31.000, 31.000) * PIXEL,
        Vec3d(8.000, 16.000, 30.000) * PIXEL to Vec3d(24.000, 31.000, 31.000) * PIXEL,
        Vec3d(31.000, 3.000, 3.000) * PIXEL to Vec3d(32.000, 13.000, 13.000) * PIXEL,
        Vec3d(22.000, 2.000, 29.000) * PIXEL to Vec3d(24.000, 12.000, 31.000) * PIXEL,
        Vec3d(2.000, 2.000, 29.000) * PIXEL to Vec3d(4.000, 12.000, 31.000) * PIXEL,
        Vec3d(-8.000, 2.000, 29.000) * PIXEL to Vec3d(-6.000, 12.000, 31.000) * PIXEL,
        Vec3d(-16.000, 0.000, -16.000) * PIXEL to Vec3d(32.000, 2.000, 32.000) * PIXEL,
        Vec3d(1.000, 0.000, -32.000) * PIXEL to Vec3d(15.000, 2.000, -16.000) * PIXEL,
        Vec3d(4.000, 16.000, -28.000) * PIXEL to Vec3d(12.000, 30.000, -20.000) * PIXEL,
        Vec3d(1.000, 0.000, -32.000) * PIXEL to Vec3d(15.000, 2.000, -16.000) * PIXEL,
        Vec3d(2.000, 2.000, -31.000) * PIXEL to Vec3d(14.000, 16.000, -16.000) * PIXEL,
        Vec3d(1.000, 2.000, -16.000) * PIXEL to Vec3d(15.000, 17.000, -15.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}