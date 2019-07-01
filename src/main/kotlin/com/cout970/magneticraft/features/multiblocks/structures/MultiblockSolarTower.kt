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


object MultiblockSolarTower : Multiblock() {

    override val name: String = "solar_tower"
    override val size: BlockPos = BlockPos(3, 3, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val B = baseBlock()
        val I = corrugatedIronBlock()
        val C = copperCoilBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(B, I, B), // y = 2
                listOf(I, C, I),
                listOf(B, I, B)),

            zLayers(
                listOf(B, I, B), // y = 1
                listOf(I, C, I),
                listOf(B, I, B)),

            zLayers(
                listOf(B, M, B), // y = 0
                listOf(B, B, B),
                listOf(B, B, B))
        )
    }

    override fun getControllerBlock() = Multiblocks.solarTower

    val hitbox = listOf(
        Vec3d(-16.000, 0.000, -16.000) * PIXEL to Vec3d(32.000, 6.000, 0.000) * PIXEL,
        Vec3d(-16.000, 0.000, 16.000) * PIXEL to Vec3d(32.000, 6.000, 32.000) * PIXEL,
        Vec3d(-16.000, 6.000, -16.000) * PIXEL to Vec3d(32.000, 7.000, 32.000) * PIXEL,
        Vec3d(-12.000, 44.000, -12.000) * PIXEL to Vec3d(28.000, 48.000, 28.000) * PIXEL,
        Vec3d(-6.000, 7.000, -10.000) * PIXEL to Vec3d(22.000, 44.000, -8.000) * PIXEL,
        Vec3d(-6.000, 7.000, 24.000) * PIXEL to Vec3d(22.000, 44.000, 26.000) * PIXEL,
        Vec3d(-10.000, 7.000, -6.000) * PIXEL to Vec3d(-8.000, 44.000, 22.000) * PIXEL,
        Vec3d(24.000, 7.000, -6.000) * PIXEL to Vec3d(26.000, 44.000, 22.000) * PIXEL,
        Vec3d(-8.000, 7.000, -8.000) * PIXEL to Vec3d(24.000, 44.000, 24.000) * PIXEL,
        Vec3d(-16.000, 0.000, 0.000) * PIXEL to Vec3d(0.000, 6.000, 16.000) * PIXEL,
        Vec3d(16.000, 0.000, 0.000) * PIXEL to Vec3d(32.000, 6.000, 16.000) * PIXEL,
        Vec3d(4.000, 0.000, 4.000) * PIXEL to Vec3d(12.000, 6.000, 12.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitbox

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}