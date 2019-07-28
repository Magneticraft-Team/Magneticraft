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

object MultiblockBigElectricFurnace : Multiblock() {

    override val name: String = "big_electric_furnace"
    override val size: BlockPos = BlockPos(3, 2, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val I = grateBlock()
        val C = columnBlock(EnumFacing.UP)
        val P = copperCoilBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(C, I, C), // y = 1
                listOf(I, P, I),
                listOf(C, I, C)),

            zLayers(
                listOf(C, M, C), // y = 0
                listOf(I, I, I),
                listOf(C, I, C))
        )
    }

    override fun getControllerBlock() = Multiblocks.bigElectricFurnace

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(16.000, 8.000, -8.000) * PIXEL to Vec3d(22.000, 30.000, 24.000) * PIXEL,
        Vec3d(0.000, 22.000, -8.000) * PIXEL to Vec3d(16.000, 30.000, 24.000) * PIXEL,
        Vec3d(-8.000, 8.000, 18.000) * PIXEL to Vec3d(-6.000, 30.000, 22.000) * PIXEL,
        Vec3d(-8.000, 8.000, -6.000) * PIXEL to Vec3d(-6.000, 30.000, -2.000) * PIXEL,
        Vec3d(-7.000, 8.000, 6.000) * PIXEL to Vec3d(-6.000, 30.000, 10.000) * PIXEL,
        Vec3d(-8.000, 30.000, 18.000) * PIXEL to Vec3d(24.000, 32.000, 22.000) * PIXEL,
        Vec3d(-8.000, 30.000, -6.000) * PIXEL to Vec3d(24.000, 32.000, -2.000) * PIXEL,
        Vec3d(-7.000, 30.000, 6.000) * PIXEL to Vec3d(23.000, 31.000, 10.000) * PIXEL,
        Vec3d(-2.000, 8.000, 24.000) * PIXEL to Vec3d(0.000, 22.000, 26.000) * PIXEL,
        Vec3d(16.000, 8.000, 24.000) * PIXEL to Vec3d(18.000, 22.000, 26.000) * PIXEL,
        Vec3d(-2.000, 22.000, 24.000) * PIXEL to Vec3d(18.000, 30.000, 26.000) * PIXEL,
        Vec3d(22.000, 8.000, 18.000) * PIXEL to Vec3d(24.000, 30.000, 22.000) * PIXEL,
        Vec3d(22.000, 8.000, -6.000) * PIXEL to Vec3d(24.000, 30.000, -2.000) * PIXEL,
        Vec3d(22.000, 8.000, 6.000) * PIXEL to Vec3d(23.000, 30.000, 10.000) * PIXEL,
        Vec3d(0.000, 17.082, 23.654) * PIXEL to Vec3d(5.000, 22.000, 25.000) * PIXEL,
        Vec3d(11.000, 17.082, 23.654) * PIXEL to Vec3d(16.000, 22.000, 25.000) * PIXEL,
        Vec3d(5.500, 17.082, 23.654) * PIXEL to Vec3d(10.500, 22.000, 25.000) * PIXEL,
        Vec3d(-2.000, 0.000, -10.000) * PIXEL to Vec3d(0.000, 22.000, -8.000) * PIXEL,
        Vec3d(16.000, 0.000, -10.000) * PIXEL to Vec3d(18.000, 22.000, -8.000) * PIXEL,
        Vec3d(-2.000, 22.000, -10.000) * PIXEL to Vec3d(18.000, 30.000, -8.000) * PIXEL,
        Vec3d(-6.000, 8.000, -8.000) * PIXEL to Vec3d(0.000, 30.000, 24.000) * PIXEL,
        Vec3d(-9.000, 0.000, -10.000) * PIXEL to Vec3d(25.000, 8.000, 26.000) * PIXEL,
        Vec3d(11.000, 17.082, -9.346) * PIXEL to Vec3d(16.000, 22.000, -8.000) * PIXEL,
        Vec3d(0.000, 17.082, -9.346) * PIXEL to Vec3d(5.000, 22.000, -8.000) * PIXEL,
        Vec3d(5.367, 17.082, -9.346) * PIXEL to Vec3d(10.367, 22.000, -8.000) * PIXEL,
        Vec3d(5.000, 30.000, 5.000) * PIXEL to Vec3d(11.000, 32.000, 11.000) * PIXEL,
        Vec3d(0.000, 8.000, -7.000) * PIXEL to Vec3d(16.000, 22.000, 23.000) * PIXEL,
        Vec3d(0.000, 9.000, -16.000) * PIXEL to Vec3d(16.000, 13.000, -7.000) * PIXEL,
        Vec3d(0.000, 9.000, 23.000) * PIXEL to Vec3d(16.000, 13.000, 32.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}