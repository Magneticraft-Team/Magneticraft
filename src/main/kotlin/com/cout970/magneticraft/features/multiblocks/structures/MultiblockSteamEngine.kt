package com.cout970.magneticraft.features.multiblocks.structures

import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.rotateBox
import com.cout970.magneticraft.misc.vector.times
import com.cout970.magneticraft.systems.multiblocks.*
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks


/**
 * Created by cout970 on 2017/07/17.
 */
object MultiblockSteamEngine : Multiblock() {

    override val name: String = "steam_engine"
    override val size: BlockPos = BlockPos(3, 4, 4)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(0, 1, 0)

    init {
        val I = IgnoreBlockComponent
        val C = columnBlock(EnumFacing.UP)
        val B = ofBlock(Blocks.BRICK_BLOCK)
        val V = copperCoilBlock()
        val G = grateBlock()
        val N = baseBlock()
        val O = corrugatedIronBlock()
        val M = mainBlockOf(controllerBlock)

        scheme = yLayers(
            zLayers(
                listOf(N, I, I), // y = 2
                listOf(N, I, I),
                listOf(N, I, I),
                listOf(I, I, I)),

            zLayers(
                listOf(O, I, I), // y = 1
                listOf(C, I, I),
                listOf(G, G, I),
                listOf(I, G, I)),

            zLayers(
                listOf(M, I, I), // y = 0
                listOf(B, I, I),
                listOf(B, G, V),
                listOf(B, G, C)),

            zLayers(
                listOf(I, I, I), // y = -1
                listOf(I, I, I),
                listOf(I, G, I),
                listOf(I, G, I))
        )
    }

    override fun getControllerBlock() = Multiblocks.steamEngine

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()

    val hitbox = listOf(
        Vec3d(19.500, 6.000, 17.500) * PIXEL to Vec3d(28.500, 7.000, 26.500) * PIXEL,
        Vec3d(20.000, 7.000, 18.000) * PIXEL to Vec3d(28.000, 19.000, 26.000) * PIXEL,
        Vec3d(22.500, 20.000, 20.500) * PIXEL to Vec3d(25.500, 22.000, 23.500) * PIXEL,
        Vec3d(19.500, 19.000, 17.500) * PIXEL to Vec3d(28.500, 20.000, 26.500) * PIXEL,
        Vec3d(16.000, 0.000, -24.000) * PIXEL to Vec3d(32.000, 4.000, 32.000) * PIXEL,
        Vec3d(18.000, 4.000, 16.000) * PIXEL to Vec3d(30.000, 6.000, 28.000) * PIXEL,
        Vec3d(20.000, 32.000, 0.000) * PIXEL to Vec3d(28.000, 34.000, 8.000) * PIXEL,
        Vec3d(21.000, 34.000, 2.000) * PIXEL to Vec3d(22.000, 39.000, 6.000) * PIXEL,
        Vec3d(26.000, 34.000, 2.000) * PIXEL to Vec3d(27.000, 39.000, 6.000) * PIXEL,
        Vec3d(22.000, 36.000, -15.000) * PIXEL to Vec3d(26.000, 40.000, 23.000) * PIXEL,
        Vec3d(11.000, 6.000, -18.000) * PIXEL to Vec3d(16.000, 10.000, -14.000) * PIXEL,
        Vec3d(23.500, 14.000, 21.500) * PIXEL to Vec3d(24.500, 34.000, 22.500) * PIXEL,
        Vec3d(23.000, 33.000, 21.000) * PIXEL to Vec3d(25.000, 36.000, 23.000) * PIXEL,
        Vec3d(0.000, 7.000, -17.000) * PIXEL to Vec3d(11.000, 9.000, -15.000) * PIXEL,
        Vec3d(-1.000, 5.000, -19.000) * PIXEL to Vec3d(0.000, 11.000, -13.000) * PIXEL,
        Vec3d(16.000, 4.000, -19.000) * PIXEL to Vec3d(20.000, 11.000, -13.000) * PIXEL,
        Vec3d(21.000, 13.084, -16.414) * PIXEL to Vec3d(22.000, 35.087, -13.500) * PIXEL,
        Vec3d(21.000, 35.000, -15.000) * PIXEL to Vec3d(22.000, 38.000, -13.000) * PIXEL,
        Vec3d(20.000, 8.000, -17.000) * PIXEL to Vec3d(21.000, 14.000, -15.000) * PIXEL,
        Vec3d(20.000, 4.000, 29.000) * PIXEL to Vec3d(28.000, 12.000, 32.000) * PIXEL,
        Vec3d(-16.000, 0.000, -32.000) * PIXEL to Vec3d(0.000, 4.000, -6.000) * PIXEL,
        // See @ModuleSteamEngineMb
        Vec3d(-10.000, 12.000, -10.000) * PIXEL to Vec3d(-6.000, 17.000, -6.000) * PIXEL,
        Vec3d(-14.000, 1.000, -8.000) * PIXEL to Vec3d(-2.000, 13.000, 0.000) * PIXEL,
        Vec3d(21.000, 4.000, 26.000) * PIXEL to Vec3d(27.000, 11.000, 29.000) * PIXEL,
        Vec3d(23.000, 4.000, -23.000) * PIXEL to Vec3d(31.000, 10.000, -5.000) * PIXEL,
        Vec3d(23.500, 6.000, 13.500) * PIXEL to Vec3d(24.500, 34.000, 14.500) * PIXEL,
        Vec3d(22.500, 33.000, 13.000) * PIXEL to Vec3d(23.500, 36.000, 15.000) * PIXEL,
        Vec3d(24.500, 33.000, 13.000) * PIXEL to Vec3d(25.500, 36.000, 15.000) * PIXEL,
        Vec3d(22.500, 33.000, 15.000) * PIXEL to Vec3d(23.500, 34.000, 22.000) * PIXEL,
        Vec3d(24.500, 33.000, 15.000) * PIXEL to Vec3d(25.500, 34.000, 22.000) * PIXEL,
        Vec3d(23.000, 4.000, 13.000) * PIXEL to Vec3d(25.000, 11.000, 15.000) * PIXEL,
        Vec3d(22.000, 11.000, 26.000) * PIXEL to Vec3d(26.000, 17.000, 29.000) * PIXEL,
        Vec3d(23.500, 15.500, 27.000) * PIXEL to Vec3d(24.500, 19.500, 28.000) * PIXEL,
        Vec3d(20.000, 18.000, 27.000) * PIXEL to Vec3d(28.000, 19.000, 28.000) * PIXEL,
        Vec3d(20.000, 4.000, 27.000) * PIXEL to Vec3d(21.000, 18.000, 28.000) * PIXEL,
        Vec3d(18.000, 6.000, -14.070) * PIXEL to Vec3d(19.000, 9.325, 23.977) * PIXEL,
        Vec3d(18.500, 5.000, 23.000) * PIXEL to Vec3d(19.500, 9.000, 25.000) * PIXEL,
        Vec3d(27.000, 4.000, 27.000) * PIXEL to Vec3d(28.000, 18.000, 28.000) * PIXEL,
        // See @ModuleSteamEngineMb
        Vec3d(-13.000, 4.000, -24.000) * PIXEL to Vec3d(-5.000, 5.000, -12.000) * PIXEL,
        Vec3d(11.500, 2.000, -5.000) * PIXEL to Vec3d(14.500, 14.000, -2.000) * PIXEL,
        Vec3d(11.500, 19.000, -22.000) * PIXEL to Vec3d(14.500, 22.000, -10.000) * PIXEL,
        Vec3d(11.500, 12.031, -29.930) * PIXEL to Vec3d(14.500, 21.930, -20.031) * PIXEL,
        Vec3d(11.500, -6.000, -22.000) * PIXEL to Vec3d(14.500, -3.000, -10.000) * PIXEL,
        Vec3d(11.500, 12.031, -11.969) * PIXEL to Vec3d(14.500, 21.930, -2.070) * PIXEL,
        Vec3d(11.500, -5.930, -29.930) * PIXEL to Vec3d(14.500, 3.969, -20.031) * PIXEL,
        Vec3d(11.500, -5.930, -11.969) * PIXEL to Vec3d(14.500, 3.969, -2.070) * PIXEL,
        Vec3d(11.500, 2.000, -30.000) * PIXEL to Vec3d(14.500, 14.000, -27.000) * PIXEL,
        Vec3d(12.000, -3.469, -21.516) * PIXEL to Vec3d(14.000, 19.469, -10.484) * PIXEL,
        Vec3d(12.000, -3.469, -21.516) * PIXEL to Vec3d(14.000, 19.469, -10.484) * PIXEL,
        Vec3d(12.000, 2.484, -27.469) * PIXEL to Vec3d(14.000, 13.516, -4.531) * PIXEL,
        Vec3d(12.000, 2.484, -27.469) * PIXEL to Vec3d(14.000, 13.516, -4.531) * PIXEL,
        Vec3d(21.000, 10.000, 1.000) * PIXEL to Vec3d(27.000, 32.000, 7.000) * PIXEL,
        Vec3d(20.000, 4.000, 0.000) * PIXEL to Vec3d(28.000, 10.000, 8.000) * PIXEL,
        // See @ModuleSteamEngineMb
        Vec3d(-10.621, 7.379, -15.000) * PIXEL to Vec3d(-6.379, 11.621, -13.000) * PIXEL,
        Vec3d(-10.621, 7.379, -15.000) * PIXEL to Vec3d(-6.379, 11.621, -13.000) * PIXEL,
        Vec3d(-11.000, 9.000, -15.000) * PIXEL to Vec3d(-6.000, 10.000, -13.000) * PIXEL,
        Vec3d(-9.000, 7.000, -15.000) * PIXEL to Vec3d(-8.000, 12.000, -13.000) * PIXEL,
        Vec3d(-9.919, 1.999, -18.000) * PIXEL to Vec3d(-7.081, 7.001, -16.000) * PIXEL,
        Vec3d(-11.001, 3.081, -18.000) * PIXEL to Vec3d(-5.999, 5.919, -16.000) * PIXEL,
        Vec3d(-11.001, 3.081, -18.000) * PIXEL to Vec3d(-5.999, 5.919, -16.000) * PIXEL,
        Vec3d(-9.919, 1.999, -18.000) * PIXEL to Vec3d(-7.081, 7.001, -16.000) * PIXEL,
        Vec3d(-9.919, 2.999, -21.000) * PIXEL to Vec3d(-7.081, 8.001, -19.000) * PIXEL,
        Vec3d(-11.001, 4.081, -21.000) * PIXEL to Vec3d(-5.999, 6.919, -19.000) * PIXEL,
        Vec3d(-11.001, 4.081, -21.000) * PIXEL to Vec3d(-5.999, 6.919, -19.000) * PIXEL,
        Vec3d(-9.919, 2.999, -21.000) * PIXEL to Vec3d(-7.081, 8.001, -19.000) * PIXEL,
        Vec3d(-10.621, 7.379, -21.000) * PIXEL to Vec3d(-6.379, 11.621, -19.000) * PIXEL,
        Vec3d(-10.621, 7.379, -21.000) * PIXEL to Vec3d(-6.379, 11.621, -19.000) * PIXEL,
        Vec3d(-11.000, 9.000, -21.000) * PIXEL to Vec3d(-6.000, 10.000, -19.000) * PIXEL,
        Vec3d(-9.000, 7.000, -21.000) * PIXEL to Vec3d(-8.000, 12.000, -19.000) * PIXEL
    ).map { EnumFacing.SOUTH.rotateBox(Vec3d(0.5, 0.5, 0.5), it) + Vec3d(1.0, 0.0, 1.0) }

    override fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = hitbox
}