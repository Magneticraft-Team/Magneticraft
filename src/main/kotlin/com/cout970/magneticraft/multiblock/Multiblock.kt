package com.cout970.magneticraft.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.times
import com.cout970.magneticraft.tilerenderer.PIXEL
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 */
abstract class Multiblock {

    abstract val name: String
    abstract val size: BlockPos
    abstract val scheme: List<MultiblockLayer>
    abstract val center: BlockPos

    class MultiblockLayer(val components: List<List<IMultiblockComponent>>)

    abstract fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent>

    companion object {
        fun yLayers(vararg args: List<List<IMultiblockComponent>>): List<MultiblockLayer> = args.map { MultiblockLayer(it) }
        fun zLayers(vararg args: List<IMultiblockComponent>): List<List<IMultiblockComponent>> = listOf(*args)
    }

    open fun onActivate(data: List<BlockData>, context: MultiblockContext) = Unit
    open fun onDeactivate(data: List<BlockData>, context: MultiblockContext) = Unit

    open fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf(
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

}