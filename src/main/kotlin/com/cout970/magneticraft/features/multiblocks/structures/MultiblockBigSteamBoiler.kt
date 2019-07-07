package com.cout970.magneticraft.features.multiblocks.structures

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

object MultiblockBigSteamBoiler : Multiblock() {

    override val name: String = "big_steam_boiler"
    override val size: BlockPos = BlockPos(3, 4, 3)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val H = baseBlock()
        val I = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(I, I, I), // y = 3
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(I, I, I), // y = 2
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(I, I, I), // y = 1
                listOf(I, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(H, M, H), // y = 0
                listOf(H, H, H),
                listOf(H, H, H))
        )
    }

    override fun getControllerBlock() = Multiblocks.bigSteamBoiler

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitboxes

    val hitboxes = listOf(
        Vec3d(8.000, 53.000, -30.000) * PIXEL to Vec3d(30.000, 62.000, -8.000) * PIXEL,
        Vec3d(4.000, 62.000, -12.000) * PIXEL to Vec3d(12.000, 64.000, -4.000) * PIXEL,
        Vec3d(-14.000, 53.000, -30.000) * PIXEL to Vec3d(30.000, 62.000, 14.000) * PIXEL,
        Vec3d(30.000, 16.000, -30.000) * PIXEL to Vec3d(31.000, 17.000, 15.000) * PIXEL,
        Vec3d(-16.000, 0.000, -32.000) * PIXEL to Vec3d(32.000, 8.000, 16.000) * PIXEL,
        Vec3d(-15.000, 16.000, -31.000) * PIXEL to Vec3d(-14.000, 17.000, 14.000) * PIXEL,
        Vec3d(-15.000, 16.000, 14.000) * PIXEL to Vec3d(30.000, 17.000, 15.000) * PIXEL,
        Vec3d(-14.000, 16.000, -31.000) * PIXEL to Vec3d(31.000, 17.000, -30.000) * PIXEL,
        Vec3d(-15.000, 25.000, -31.000) * PIXEL to Vec3d(-14.000, 26.000, 14.000) * PIXEL,
        Vec3d(-14.000, 25.000, -31.000) * PIXEL to Vec3d(31.000, 26.000, -30.000) * PIXEL,
        Vec3d(30.000, 25.000, -30.000) * PIXEL to Vec3d(31.000, 26.000, 15.000) * PIXEL,
        Vec3d(-15.000, 25.000, 14.000) * PIXEL to Vec3d(30.000, 26.000, 15.000) * PIXEL,
        Vec3d(-15.000, 34.000, -31.000) * PIXEL to Vec3d(-14.000, 35.000, 14.000) * PIXEL,
        Vec3d(-14.000, 34.000, -31.000) * PIXEL to Vec3d(31.000, 35.000, -30.000) * PIXEL,
        Vec3d(30.000, 34.000, -30.000) * PIXEL to Vec3d(31.000, 35.000, 15.000) * PIXEL,
        Vec3d(-15.000, 34.000, 14.000) * PIXEL to Vec3d(30.000, 35.000, 15.000) * PIXEL,
        Vec3d(-15.000, 43.000, -31.000) * PIXEL to Vec3d(-14.000, 44.000, 14.000) * PIXEL,
        Vec3d(-14.000, 43.000, -31.000) * PIXEL to Vec3d(31.000, 44.000, -30.000) * PIXEL,
        Vec3d(30.000, 43.000, -30.000) * PIXEL to Vec3d(31.000, 44.000, 15.000) * PIXEL,
        Vec3d(-15.000, 43.000, 14.000) * PIXEL to Vec3d(30.000, 44.000, 15.000) * PIXEL,
        Vec3d(-15.000, 52.000, -31.000) * PIXEL to Vec3d(-14.000, 53.000, 14.000) * PIXEL,
        Vec3d(-14.000, 52.000, -31.000) * PIXEL to Vec3d(31.000, 53.000, -30.000) * PIXEL,
        Vec3d(30.000, 52.000, -30.000) * PIXEL to Vec3d(31.000, 53.000, 15.000) * PIXEL,
        Vec3d(-15.000, 52.000, 14.000) * PIXEL to Vec3d(30.000, 53.000, 15.000) * PIXEL,
        Vec3d(-14.000, 53.000, -30.000) * PIXEL to Vec3d(8.000, 62.000, -8.000) * PIXEL,
        Vec3d(-14.000, 53.000, -8.000) * PIXEL to Vec3d(8.000, 62.000, 14.000) * PIXEL,
        Vec3d(8.000, 53.000, -8.000) * PIXEL to Vec3d(30.000, 62.000, 14.000) * PIXEL,
        Vec3d(-14.000, 44.000, -30.000) * PIXEL to Vec3d(30.000, 53.000, 14.000) * PIXEL,
        Vec3d(-14.000, 35.000, -30.000) * PIXEL to Vec3d(30.000, 44.000, 14.000) * PIXEL,
        Vec3d(-14.000, 26.000, -30.000) * PIXEL to Vec3d(30.000, 35.000, 14.000) * PIXEL,
        Vec3d(-14.000, 17.000, -30.000) * PIXEL to Vec3d(30.000, 26.000, 14.000) * PIXEL,
        Vec3d(-14.000, 8.000, -30.000) * PIXEL to Vec3d(30.000, 17.000, 14.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) }

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = emptyList()
}