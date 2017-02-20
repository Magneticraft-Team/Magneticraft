package com.cout970.magneticraft.multiblock.impl



import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_CENTER
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.multiblock.BlockSifter
import com.cout970.magneticraft.multiblock.BlockData
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.components.ContextBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.tilerenderer.PIXEL
import com.cout970.magneticraft.util.vector.times
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 20/08/2016.
 */
object MultiblockSifter : Multiblock() {

    override val name: String = "sifter"
    override val size: BlockPos = BlockPos(3, 2, 4)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(1, 0, 0)

    init {
        val replacement = BlockSifter.defaultState
                .withProperty(PROPERTY_CENTER, false)
                .withProperty(PROPERTY_ACTIVE, true)

        val S: IMultiblockComponent = SingleBlockComponent(
                BlockMesh.defaultState, replacement)

        val B: IMultiblockComponent = SingleBlockComponent(BlockMachineBlock.defaultState, replacement)

        val L: IMultiblockComponent = SingleBlockComponent(
                BlockBurntLimestone.defaultState.withProperty(BlockBurntLimestone.LIMESTONE_STATES,
                        BlockLimestone.LimestoneStates.BRICK), replacement)

        val C: IMultiblockComponent = ContextBlockComponent(
                { ctx ->
                    BlockMachineBlockSupportColumn.defaultState.withProperty(
                            BlockMachineBlockSupportColumn.PROPERTY_STATES,
                            BlockMachineBlockSupportColumn.States.fromAxis(ctx.facing.axis))
                }, ItemStack(BlockMachineBlockSupportColumn, 1, 1), replacement)

        val M: IMultiblockComponent = MainBlockComponent(BlockSifter) { context, state, activate ->
            if (activate) {
                BlockSifter.defaultState
                        .withProperty(PROPERTY_ACTIVE, true)
                        .withProperty(PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            } else {
                BlockSifter.defaultState
                        .withProperty(PROPERTY_ACTIVE, false)
                        .withProperty(PROPERTY_CENTER, true)
                        .withProperty(PROPERTY_DIRECTION, context.facing)
            }
        }

        scheme = yLayers(
                zLayers(listOf(C, S, C),
                        listOf(C, S, C),
                        listOf(C, S, C),
                        listOf(C, B, C)),
                zLayers(listOf(L, M, B),
                        listOf(L, L, B),
                        listOf(L, L, B),
                        listOf(L, L, B)))
    }

    override fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf(
            Vec3d(23.000, 0.000, 57.000) * PIXEL to Vec3d(27.000, 12.000, 61.000) * PIXEL,
            Vec3d(-13.000, 0.000, 2.000) * PIXEL to Vec3d(29.000, 28.000, 12.000) * PIXEL,
            Vec3d(-4.000, 15.005, 26.994) * PIXEL to Vec3d(20.000, 17.882, 45.000) * PIXEL,
            Vec3d(-4.000, 20.005, 11.994) * PIXEL to Vec3d(20.000, 22.882, 30.000) * PIXEL,
            Vec3d(-4.000, 10.005, 41.994) * PIXEL to Vec3d(20.000, 12.882, 60.000) * PIXEL,
            Vec3d(-5.000, 19.901, 11.000) * PIXEL to Vec3d(-4.000, 23.981, 31.099) * PIXEL,
            Vec3d(20.000, 19.901, 11.000) * PIXEL to Vec3d(21.000, 23.981, 31.099) * PIXEL,
            Vec3d(-5.000, 14.901, 10.087) * PIXEL to Vec3d(-4.000, 20.653, 46.099) * PIXEL,
            Vec3d(20.000, 14.901, 10.087) * PIXEL to Vec3d(21.000, 20.653, 46.099) * PIXEL,
            Vec3d(-5.000, 10.005, 11.169) * PIXEL to Vec3d(-4.000, 17.221, 61.105) * PIXEL,
            Vec3d(20.000, 10.005, 11.169) * PIXEL to Vec3d(21.000, 17.221, 61.105) * PIXEL,
            Vec3d(21.000, 0.000, 61.000) * PIXEL to Vec3d(29.000, 14.000, 64.000) * PIXEL,
            Vec3d(-13.000, 0.000, 61.000) * PIXEL to Vec3d(-5.000, 14.000, 64.000) * PIXEL,
            Vec3d(22.000, 15.605, 10.058) * PIXEL to Vec3d(28.000, 27.369, 39.058) * PIXEL,
            Vec3d(1.000, 26.960, 1.060) * PIXEL to Vec3d(15.000, 32.000, 3.710) * PIXEL,
            Vec3d(1.000, 26.960, 8.290) * PIXEL to Vec3d(15.000, 32.000, 10.940) * PIXEL,
            Vec3d(0.060, 26.960, 2.000) * PIXEL to Vec3d(2.710, 32.000, 10.000) * PIXEL,
            Vec3d(13.290, 26.960, 2.000) * PIXEL to Vec3d(15.940, 32.000, 10.000) * PIXEL,
            Vec3d(0.000, 31.000, 1.000) * PIXEL to Vec3d(1.000, 32.000, 11.000) * PIXEL,
            Vec3d(15.000, 31.000, 1.000) * PIXEL to Vec3d(16.000, 32.000, 11.000) * PIXEL,
            Vec3d(1.000, 31.000, 10.000) * PIXEL to Vec3d(15.000, 32.000, 11.000) * PIXEL,
            Vec3d(1.000, 31.000, 1.000) * PIXEL to Vec3d(15.000, 32.000, 2.000) * PIXEL,
            Vec3d(0.000, 28.000, 10.000) * PIXEL to Vec3d(1.000, 31.000, 11.000) * PIXEL,
            Vec3d(1.000, 28.000, 10.000) * PIXEL to Vec3d(2.000, 31.000, 11.000) * PIXEL,
            Vec3d(14.000, 28.000, 10.000) * PIXEL to Vec3d(15.000, 31.000, 11.000) * PIXEL,
            Vec3d(15.000, 28.000, 10.000) * PIXEL to Vec3d(16.000, 31.000, 11.000) * PIXEL,
            Vec3d(15.000, 28.000, 9.000) * PIXEL to Vec3d(16.000, 31.000, 10.000) * PIXEL,
            Vec3d(0.000, 28.000, 9.000) * PIXEL to Vec3d(1.000, 31.000, 10.000) * PIXEL,
            Vec3d(0.000, 28.000, 2.000) * PIXEL to Vec3d(1.000, 31.000, 3.000) * PIXEL,
            Vec3d(0.000, 28.000, 1.000) * PIXEL to Vec3d(1.000, 31.000, 2.000) * PIXEL,
            Vec3d(1.000, 28.000, 1.000) * PIXEL to Vec3d(2.000, 31.000, 2.000) * PIXEL,
            Vec3d(14.000, 28.000, 1.000) * PIXEL to Vec3d(15.000, 31.000, 2.000) * PIXEL,
            Vec3d(15.000, 28.000, 1.000) * PIXEL to Vec3d(16.000, 31.000, 2.000) * PIXEL,
            Vec3d(15.000, 28.000, 2.000) * PIXEL to Vec3d(16.000, 31.000, 3.000) * PIXEL,
            Vec3d(-2.000, 0.000, 0.000) * PIXEL to Vec3d(18.000, 28.000, 2.000) * PIXEL,
            Vec3d(-1.000, 25.342, 11.412) * PIXEL to Vec3d(17.000, 26.860, 16.489) * PIXEL,
            Vec3d(-1.000, 22.359, 11.099) * PIXEL to Vec3d(-0.000, 25.865, 16.385) * PIXEL,
            Vec3d(16.000, 22.359, 11.099) * PIXEL to Vec3d(17.000, 25.865, 16.385) * PIXEL,
            Vec3d(-0.000, 22.672, 12.093) * PIXEL to Vec3d(16.000, 25.761, 13.401) * PIXEL,
            Vec3d(-7.000, 0.000, 57.000) * PIXEL to Vec3d(23.000, 2.000, 61.000) * PIXEL,
            Vec3d(-7.000, 0.000, 42.000) * PIXEL to Vec3d(23.000, 2.000, 46.000) * PIXEL,
            Vec3d(-7.000, 0.000, 27.000) * PIXEL to Vec3d(23.000, 2.000, 31.000) * PIXEL,
            Vec3d(-7.000, 0.000, 12.000) * PIXEL to Vec3d(23.000, 2.000, 16.000) * PIXEL,
            Vec3d(23.000, 0.000, 42.000) * PIXEL to Vec3d(27.000, 15.000, 46.000) * PIXEL,
            Vec3d(23.000, 0.000, 12.000) * PIXEL to Vec3d(27.000, 23.000, 16.000) * PIXEL,
            Vec3d(23.000, 0.000, 27.000) * PIXEL to Vec3d(27.000, 19.000, 31.000) * PIXEL,
            Vec3d(-11.000, 0.000, 12.000) * PIXEL to Vec3d(-7.000, 23.000, 16.000) * PIXEL,
            Vec3d(-11.000, 0.000, 27.000) * PIXEL to Vec3d(-7.000, 19.000, 31.000) * PIXEL,
            Vec3d(-11.000, 0.000, 42.000) * PIXEL to Vec3d(-7.000, 15.000, 46.000) * PIXEL,
            Vec3d(-11.000, 0.000, 57.000) * PIXEL to Vec3d(-7.000, 12.000, 61.000) * PIXEL,
            Vec3d(-7.000, 5.000, 12.000) * PIXEL to Vec3d(23.000, 9.000, 61.000) * PIXEL,
            Vec3d(4.000, 3.000, 48.000) * PIXEL to Vec3d(12.000, 5.000, 56.000) * PIXEL,
            Vec3d(4.000, 3.000, 33.000) * PIXEL to Vec3d(12.000, 5.000, 41.000) * PIXEL,
            Vec3d(4.000, 3.000, 18.000) * PIXEL to Vec3d(12.000, 5.000, 26.000) * PIXEL,
            Vec3d(-7.000, 9.000, 29.000) * PIXEL to Vec3d(23.000, 11.000, 44.000) * PIXEL,
            Vec3d(-7.000, 9.000, 12.000) * PIXEL to Vec3d(23.000, 13.000, 29.000) * PIXEL,
            Vec3d(1.500, 27.200, 3.000) * PIXEL to Vec3d(14.500, 28.200, 9.000) * PIXEL,
            Vec3d(22.000, 8.364, 37.910) * PIXEL to Vec3d(28.000, 19.292, 64.030) * PIXEL,
            Vec3d(-12.000, 15.605, 10.058) * PIXEL to Vec3d(-6.000, 27.369, 39.058) * PIXEL,
            Vec3d(-12.000, 8.364, 37.910) * PIXEL to Vec3d(-6.000, 19.292, 64.030) * PIXEL
    )

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}