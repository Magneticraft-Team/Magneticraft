package com.cout970.magneticraft.multiblock.impl

import BlockSifter
import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.times
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_CENTER
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.block.decoration.BlockMachineBlock
import com.cout970.magneticraft.block.decoration.BlockMachineBlockSupportColumn
import com.cout970.magneticraft.block.decoration.BlockMesh
import com.cout970.magneticraft.multiblock.BlockData
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.components.ContextBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.tilerenderer.PIXEL
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
    override val size: BlockPos = BlockPos(1, 2, 4)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(0, 1, 0)

    init {
        val replacement = BlockSifter.defaultState
                .withProperty(PROPERTY_CENTER, false)
                .withProperty(PROPERTY_ACTIVE, true)

        val S: IMultiblockComponent = SingleBlockComponent(
                BlockMesh.defaultState, replacement)

        val B: IMultiblockComponent = SingleBlockComponent(BlockMachineBlock.defaultState, replacement)

        val L: IMultiblockComponent = SingleBlockComponent(BlockMachineBlock.defaultState, replacement)

        val C: IMultiblockComponent = ContextBlockComponent(
                { ctx ->
                    BlockMachineBlockSupportColumn.defaultState.withProperty(BlockMachineBlockSupportColumn.PROPERTY_STATES,
                            BlockMachineBlockSupportColumn.States.fromAxis(ctx.facing.rotateY().axis))
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
                        listOf(C, M, C)),
                zLayers(listOf(L, L, B),
                        listOf(L, L, B),
                        listOf(L, L, B),
                        listOf(L, L, B)))
    }

    override fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf(
            Vec3d(-14.0, 4.0, 1.0) * PIXEL to Vec3d(-3.0, 20.0, 15.0) * PIXEL,
            Vec3d(2.0, 0.0, 2.0) * PIXEL to Vec3d(14.0, 14.0, 14.0) * PIXEL,
            Vec3d(2.0, 24.0, 2.0) * PIXEL to Vec3d(14.0, 42.0, 3.0) * PIXEL,
            Vec3d(0.0, 24.0, 3.0) * PIXEL to Vec3d(3.0, 42.0, 4.0) * PIXEL,
            Vec3d(0.0, 24.0, 12.0) * PIXEL to Vec3d(3.0, 42.0, 13.0) * PIXEL,
            Vec3d(2.0, 24.0, 13.0) * PIXEL to Vec3d(14.0, 42.0, 14.0) * PIXEL,
            Vec3d(-7.0, 25.0, 0.9000000953674316) * PIXEL to Vec3d(23.0, 28.0, 3.9000000953674316) * PIXEL,
            Vec3d(-6.0, 24.0, 0.0) * PIXEL to Vec3d(-3.0, 29.0, 4.0) * PIXEL,
            Vec3d(-11.0, 1.0, 3.0) * PIXEL to Vec3d(-1.0, 32.0, 13.0) * PIXEL,
            Vec3d(15.0, 26.0, 4.0) * PIXEL to Vec3d(25.0, 54.0, 12.0) * PIXEL,
            Vec3d(19.0, 24.0, 0.0) * PIXEL to Vec3d(22.0, 29.0, 4.0) * PIXEL,
            Vec3d(19.0, 4.0, 1.0) * PIXEL to Vec3d(30.0, 20.0, 15.0) * PIXEL,
            Vec3d(17.0, 1.0, 3.0) * PIXEL to Vec3d(27.0, 32.0, 13.0) * PIXEL,
            Vec3d(-11.0, 54.0, 2.0) * PIXEL to Vec3d(27.0, 62.0, 14.0) * PIXEL,
            Vec3d(-9.0, 26.0, 4.0) * PIXEL to Vec3d(1.0, 54.0, 12.0) * PIXEL,
            Vec3d(-16.0, 0.0, 0.0) * PIXEL to Vec3d(-2.0, 4.0, 16.0) * PIXEL,
            Vec3d(18.0, 0.0, 0.0) * PIXEL to Vec3d(32.0, 4.0, 16.0) * PIXEL,
            Vec3d(-7.0, 25.0, 12.099999904632568) * PIXEL to Vec3d(23.0, 28.0, 15.099999904632568) * PIXEL,
            Vec3d(19.0, 24.0, 12.0) * PIXEL to Vec3d(22.0, 29.0, 16.0) * PIXEL,
            Vec3d(-6.0, 24.0, 12.0) * PIXEL to Vec3d(-3.0, 29.0, 16.0) * PIXEL,
            Vec3d(13.0, 24.0, 12.0) * PIXEL to Vec3d(16.0, 42.0, 13.0) * PIXEL,
            Vec3d(13.0, 24.0, 3.0) * PIXEL to Vec3d(16.0, 42.0, 4.0) * PIXEL,
            Vec3d(-2.0, 0.0, 3.0) * PIXEL to Vec3d(-1.0, 1.0, 13.0) * PIXEL,
            Vec3d(17.0, 0.0, 3.0) * PIXEL to Vec3d(18.0, 1.0, 13.0) * PIXEL,
            Vec3d(27.0, 18.0, 4.0) * PIXEL to Vec3d(31.0, 30.0, 12.0) * PIXEL,
            Vec3d(31.0, 22.0, 6.0) * PIXEL to Vec3d(32.0, 26.0, 10.0) * PIXEL,
            Vec3d(3.0, 22.0, 3.0) * PIXEL to Vec3d(13.0, 36.0, 13.0) * PIXEL,
            Vec3d(6.5, 36.0, 6.5) * PIXEL to Vec3d(9.5, 64.0, 9.5) * PIXEL
    )

    override fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent> = listOf()
}