package com.cout970.magneticraft.multiblock


import com.cout970.magneticraft.util.vector.*
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
        fun yLayers(vararg args: List<List<IMultiblockComponent>>): List<MultiblockLayer> = args.map(::MultiblockLayer)
        fun zLayers(vararg args: List<IMultiblockComponent>): List<List<IMultiblockComponent>> = listOf(*args)
    }

    open fun onActivate(data: List<BlockData>, context: MultiblockContext) = Unit
    open fun onDeactivate(data: List<BlockData>, context: MultiblockContext) = Unit

    open fun getGlobalCollisionBox(): List<AxisAlignedBB> = listOf((BlockPos.ORIGIN toAABBWith size).offset(-center))

    infix fun Vec3d.to(other: Vec3d) = AxisAlignedBB(xd, yd, zd, other.xd, other.yd, other.zd)
}