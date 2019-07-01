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

object MultiblockSolarMirror : Multiblock() {

    override val name: String = "solar_mirror"
    override val size: BlockPos = BlockPos(3, 3, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val N = airBlock()
        val B = baseBlock()
        val I = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(N, N, N), // y = 2
                listOf(N, I, N),
                listOf(N, N, N)),

            zLayers(
                listOf(N, N, N), // y = 1
                listOf(N, I, N),
                listOf(N, N, N)),

            zLayers(
                listOf(N, M, N), // y = 0
                listOf(B, B, B),
                listOf(N, B, N))
        )
    }

    override fun getControllerBlock() = Multiblocks.solarMirror

    val hitbox = listOf(
        Vec3d(-9.000, 0.000, 6.000) * PIXEL to Vec3d(25.000, 2.000, 10.000) * PIXEL,
        Vec3d(6.000, 0.000, -10.000) * PIXEL to Vec3d(10.000, 2.000, 25.000) * PIXEL,
        Vec3d(7.000, 2.000, 7.000) * PIXEL to Vec3d(9.000, 28.000, 9.000) * PIXEL,
        Vec3d(6.500, 27.500, 6.500) * PIXEL to Vec3d(9.500, 30.500, 9.500) * PIXEL

//            Vec3d(-10.000, 12.000, -10.000) * PIXEL to Vec3d(26.000, 48.000, 26.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 1) }

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitbox

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}