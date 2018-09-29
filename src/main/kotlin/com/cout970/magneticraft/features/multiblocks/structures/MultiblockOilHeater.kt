package com.cout970.magneticraft.features.multiblocks.structures

import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.rotateBox
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.multiblocks.*
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import com.cout970.vector.extensions.times
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

object MultiblockOilHeater : Multiblock() {

    override val name: String = "oil_heater"
    override val size: BlockPos = BlockPos(3, 3, 3)
    override val scheme: List<Multiblock.MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val U = columnBlock(EnumFacing.UP)
        val H = columnBlock(EnumFacing.NORTH)
        val I = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = Multiblock.yLayers(
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

    override fun getControllerBlock() = Multiblocks.oilHeater

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(0.000, 0.000, -32.000) * PIXEL to Vec3d(48.000, 16.000, 16.000) * PIXEL,
        Vec3d(0.000, 16.000, 0.000) * PIXEL to Vec3d(48.000, 48.000, 16.000) * PIXEL,
        Vec3d(2.000, 16.000, -30.000) * PIXEL to Vec3d(24.000, 46.000, 0.000) * PIXEL,
        Vec3d(1.000, 41.000, -31.000) * PIXEL to Vec3d(2.000, 42.000, 0.000) * PIXEL,
        Vec3d(1.000, 32.000, -31.000) * PIXEL to Vec3d(2.000, 33.000, 0.000) * PIXEL,
        Vec3d(1.000, 23.000, -31.000) * PIXEL to Vec3d(2.000, 24.000, 0.000) * PIXEL,
        Vec3d(24.000, 16.000, -30.000) * PIXEL to Vec3d(46.000, 46.000, 0.000) * PIXEL,
        Vec3d(46.000, 41.000, -31.000) * PIXEL to Vec3d(47.000, 42.000, 0.000) * PIXEL,
        Vec3d(46.000, 32.000, -31.000) * PIXEL to Vec3d(47.000, 33.000, 0.000) * PIXEL,
        Vec3d(46.000, 23.000, -31.000) * PIXEL to Vec3d(47.000, 24.000, 0.000) * PIXEL,
        Vec3d(2.000, 41.000, -31.000) * PIXEL to Vec3d(46.000, 42.000, -30.000) * PIXEL,
        Vec3d(2.000, 32.000, -31.000) * PIXEL to Vec3d(46.000, 33.000, -30.000) * PIXEL,
        Vec3d(2.000, 23.000, -31.000) * PIXEL to Vec3d(46.000, 24.000, -30.000) * PIXEL,
        Vec3d(20.000, 20.000, -32.000) * PIXEL to Vec3d(28.000, 28.000, -30.000) * PIXEL,
        Vec3d(20.000, 46.000, -12.000) * PIXEL to Vec3d(28.000, 48.000, -4.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 0) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}