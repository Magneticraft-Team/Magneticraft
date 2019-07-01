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


object MultiblockContainer : Multiblock() {

    override val name: String = "container"
    override val size: BlockPos = BlockPos(3, 3, 7)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val I = airBlock()
        val B = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(

            zLayers(listOf(B, B, B), // y = 2
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B)),

            zLayers(listOf(B, B, B), // y = 1
                listOf(B, I, B),
                listOf(B, I, B),
                listOf(B, I, B),
                listOf(B, I, B),
                listOf(B, I, B),
                listOf(B, B, B)),

            zLayers(listOf(B, M, B), // y = 0
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B),
                listOf(B, B, B))
        )
    }

    override fun getControllerBlock() = Multiblocks.container


    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(-16.000, 3.000, -48.000) * PIXEL to Vec3d(-15.000, 46.000, -45.000) * PIXEL,
        Vec3d(31.000, 3.000, -48.000) * PIXEL to Vec3d(32.000, 46.000, -45.000) * PIXEL,
        Vec3d(31.000, 3.000, 61.000) * PIXEL to Vec3d(32.000, 46.000, 64.000) * PIXEL,
        Vec3d(-16.000, 3.000, 61.000) * PIXEL to Vec3d(-15.000, 46.000, 64.000) * PIXEL,
        Vec3d(-16.000, 0.000, 32.000) * PIXEL to Vec3d(32.000, 3.000, 48.000) * PIXEL,
        Vec3d(-15.000, 3.000, 32.000) * PIXEL to Vec3d(-13.000, 46.000, 48.000) * PIXEL,
        Vec3d(29.000, 3.000, 32.000) * PIXEL to Vec3d(31.000, 46.000, 48.000) * PIXEL,
        Vec3d(-16.000, 46.000, 32.000) * PIXEL to Vec3d(32.000, 48.000, 48.000) * PIXEL,
        Vec3d(-16.000, 0.000, 48.000) * PIXEL to Vec3d(32.000, 3.000, 64.000) * PIXEL,
        Vec3d(-15.000, 3.000, 48.000) * PIXEL to Vec3d(-13.000, 46.000, 64.000) * PIXEL,
        Vec3d(29.000, 3.000, 48.000) * PIXEL to Vec3d(31.000, 46.000, 64.000) * PIXEL,
        Vec3d(-16.000, 46.000, 48.000) * PIXEL to Vec3d(32.000, 48.000, 64.000) * PIXEL,
        Vec3d(-13.000, 3.000, 61.000) * PIXEL to Vec3d(8.000, 46.000, 63.000) * PIXEL,
        Vec3d(8.000, 3.000, 61.000) * PIXEL to Vec3d(29.000, 46.000, 63.000) * PIXEL,
        Vec3d(5.000, 3.000, 63.000) * PIXEL to Vec3d(6.000, 46.000, 64.000) * PIXEL,
        Vec3d(-2.000, 3.000, 63.000) * PIXEL to Vec3d(-1.000, 46.000, 64.000) * PIXEL,
        Vec3d(10.000, 3.000, 63.000) * PIXEL to Vec3d(11.000, 46.000, 64.000) * PIXEL,
        Vec3d(17.000, 3.000, 63.000) * PIXEL to Vec3d(18.000, 46.000, 64.000) * PIXEL,
        Vec3d(-16.000, 0.000, 16.000) * PIXEL to Vec3d(32.000, 3.000, 32.000) * PIXEL,
        Vec3d(-15.000, 3.000, 16.000) * PIXEL to Vec3d(-13.000, 46.000, 32.000) * PIXEL,
        Vec3d(29.000, 3.000, 16.000) * PIXEL to Vec3d(31.000, 46.000, 32.000) * PIXEL,
        Vec3d(-16.000, 46.000, 16.000) * PIXEL to Vec3d(32.000, 48.000, 32.000) * PIXEL,
        Vec3d(-16.000, 0.000, 0.000) * PIXEL to Vec3d(32.000, 3.000, 16.000) * PIXEL,
        Vec3d(-15.000, 3.000, 0.000) * PIXEL to Vec3d(-13.000, 46.000, 16.000) * PIXEL,
        Vec3d(29.000, 3.000, 0.000) * PIXEL to Vec3d(31.000, 46.000, 16.000) * PIXEL,
        Vec3d(-16.000, 46.000, 0.000) * PIXEL to Vec3d(32.000, 48.000, 16.000) * PIXEL,
        Vec3d(-16.000, 0.000, -16.000) * PIXEL to Vec3d(32.000, 3.000, 0.000) * PIXEL,
        Vec3d(-15.000, 3.000, -16.000) * PIXEL to Vec3d(-13.000, 46.000, 0.000) * PIXEL,
        Vec3d(29.000, 3.000, -16.000) * PIXEL to Vec3d(31.000, 46.000, 0.000) * PIXEL,
        Vec3d(-16.000, 46.000, -16.000) * PIXEL to Vec3d(32.000, 48.000, 0.000) * PIXEL,
        Vec3d(-16.000, 0.000, -32.000) * PIXEL to Vec3d(32.000, 3.000, -16.000) * PIXEL,
        Vec3d(-15.000, 3.000, -32.000) * PIXEL to Vec3d(-13.000, 46.000, -16.000) * PIXEL,
        Vec3d(29.000, 3.000, -32.000) * PIXEL to Vec3d(31.000, 46.000, -16.000) * PIXEL,
        Vec3d(-16.000, 46.000, -32.000) * PIXEL to Vec3d(32.000, 48.000, -16.000) * PIXEL,
        Vec3d(-16.000, 0.000, -48.000) * PIXEL to Vec3d(32.000, 3.000, -32.000) * PIXEL,
        Vec3d(-15.000, 3.000, -48.000) * PIXEL to Vec3d(-13.000, 46.000, -32.000) * PIXEL,
        Vec3d(29.000, 3.000, -48.000) * PIXEL to Vec3d(31.000, 46.000, -32.000) * PIXEL,
        Vec3d(-16.000, 46.000, -48.000) * PIXEL to Vec3d(32.000, 48.000, -32.000) * PIXEL,
        Vec3d(8.000, 3.000, -47.000) * PIXEL to Vec3d(29.000, 46.000, -45.000) * PIXEL,
        Vec3d(-13.000, 3.000, -47.000) * PIXEL to Vec3d(8.000, 46.000, -45.000) * PIXEL,
        Vec3d(5.000, 3.000, -48.000) * PIXEL to Vec3d(6.000, 46.000, -47.000) * PIXEL,
        Vec3d(-2.000, 3.000, -48.000) * PIXEL to Vec3d(-1.000, 46.000, -47.000) * PIXEL,
        Vec3d(10.000, 3.000, -48.000) * PIXEL to Vec3d(11.000, 46.000, -47.000) * PIXEL,
        Vec3d(17.000, 3.000, -48.000) * PIXEL to Vec3d(18.000, 46.000, -47.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(0, 0, 3) }


    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}