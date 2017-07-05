package com.cout970.magneticraft.multiblock.core

import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.vector.rotatePoint
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 *
 * This class acts as a Multiblock registry and adds useful functions to create, delete anc check multiblocks
 */
object MultiblockManager {

    private val multiblocks = mutableMapOf<String, Multiblock>()

    fun registerMultiblock(mb: Multiblock) {
        if (mb.name in multiblocks) throw IllegalArgumentException(
                "Multiblock with name: ${mb.name} is already registered")
        multiblocks.put(mb.name, mb)
    }

    fun getMultiblock(name: String) = multiblocks[name]

    fun registerDefaults() {
        registerMultiblock(MultiblockSolarPanel)
        registerMultiblock(MultiblockShelvingUnit)
//        registerMultiblock(MultiblockHydraulicPress)
//        registerMultiblock(MultiblockKiln)
//        registerMultiblock(MultiblockGrinder)
//        registerMultiblock(MultiblockSifter)
    }

    /**
     * This method checks if the blocks around matches the multiblock structure
     */
    fun checkMultiblockStructure(context: MultiblockContext): List<ITextComponent> {
        val list = mutableListOf<ITextComponent>()
        val data = mutableListOf<BlockData>()

        for (j in 0 until context.multiblock.size.y) {
            for (i in 0 until context.multiblock.size.x) {
                for (k in 0 until context.multiblock.size.z) {
                    val pos = applyFacing(context, BlockPos(i, j, k))
                    val comp = context.multiblock.scheme[i, j, k]
                    val res = comp.checkBlock(pos, context)
                    data.add(comp.getBlockData(pos, context))
                    if (res.isNotEmpty()) {
                        list.addAll(res)
                    }
                }
            }
        }
        list.addAll(context.multiblock.checkExtraRequirements(data, context))
        return list
    }

    /**
     * This method places a multiblock structure
     */
    fun activateMultiblockStructure(context: MultiblockContext) {
        val data = mutableListOf<BlockData>()

        try {
            for (j in 0 until context.multiblock.size.y) {
                for (i in 0 until context.multiblock.size.x) {
                    for (k in 0 until context.multiblock.size.z) {
                        val pos = applyFacing(context, BlockPos(i, j, k))
                        val comp = context.multiblock.scheme[i, j, k]
                        comp.activateBlock(pos, context)
                        data.add(comp.getBlockData(pos, context))
                    }
                }
            }
            context.multiblock.onActivate(data, context)
        } catch (e: Exception) {
            e.printStackTrace()
            deactivateMultiblockStructure(context)
        }
    }

    /**
     * This method removes a multiblock structure
     */
    fun deactivateMultiblockStructure(context: MultiblockContext) {
        val data = mutableListOf<BlockData>()

        for (j in 0 until context.multiblock.size.y) {
            for (i in 0 until context.multiblock.size.x) {
                for (k in 0 until context.multiblock.size.z) {
                    val pos = applyFacing(context, BlockPos(i, j, k))
                    val comp = context.multiblock.scheme[i, j, k]
                    comp.deactivateBlock(pos, context)
                    data.add(comp.getBlockData(pos, context))
                }
            }
        }
        context.multiblock.onDeactivate(data, context)
    }

    fun applyFacing(context: MultiblockContext, pos: BlockPos): BlockPos {
        val center = context.multiblock.center
        val origin = pos.subtract(center)
        val normalized = EnumFacing.SOUTH.rotatePoint(BlockPos.ORIGIN, origin)
        val rotated = context.facing.rotatePoint(BlockPos.ORIGIN, normalized)
        return rotated
    }
}