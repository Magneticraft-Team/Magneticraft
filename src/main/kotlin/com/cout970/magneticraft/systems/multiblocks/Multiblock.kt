package com.cout970.magneticraft.systems.multiblocks


import com.cout970.magneticraft.api.multiblock.IMultiblock
import com.cout970.magneticraft.misc.vector.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 */
abstract class Multiblock : IMultiblock {

    abstract val name: String
    abstract val size: BlockPos
    abstract val scheme: List<MultiblockLayer>
    abstract val center: BlockPos

    class MultiblockLayer(val components: List<List<IMultiblockComponent>>)

    abstract fun checkExtraRequirements(data: MutableList<BlockData>, context: MultiblockContext): List<ITextComponent>

    companion object {

        fun yLayers(vararg args: List<List<IMultiblockComponent>>): List<MultiblockLayer> =
            args.map(Multiblock::MultiblockLayer)

        fun zLayers(vararg args: List<IMultiblockComponent>): List<List<IMultiblockComponent>> = listOf(*args)
    }

    open fun onActivate(data: List<BlockData>, context: MultiblockContext) = Unit
    open fun onDeactivate(data: List<BlockData>, context: MultiblockContext) = Unit

    open fun getGlobalCollisionBoxes(): List<AxisAlignedBB> = listOf((BlockPos.ORIGIN createAABBUsing size).offset(-center))

    infix fun Vec3d.to(other: Vec3d) = AxisAlignedBB(xd, yd, zd, other.xd, other.yd, other.zd)

    override fun getMultiblockName(): String = name

    override fun getMultiblockSize(): BlockPos = size

    override fun getMultiblockCenter(): BlockPos = center
}