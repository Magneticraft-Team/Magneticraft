package com.cout970.magneticraft.multiblock

import com.cout970.magneticraft.multiblock.impl.MultiblockHydraulicPress
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.rotatePoint
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 */
object MultiblockManager {

    private val multiblocks = mutableMapOf<String, Multiblock>()

    fun registerMultiblock(mb: Multiblock) {
        if (mb.name in multiblocks) throw IllegalArgumentException("Multiblock with name: ${mb.name} is already registered")
        multiblocks.put(mb.name, mb)
    }

    fun getMultiblock(name: String) = multiblocks[name]

    fun registerDefaults() {
        registerMultiblock(MultiblockHydraulicPress)
    }

    fun checkMultiblockStructure(context: MultiblockContext): List<ITextComponent> {
        val list = mutableListOf<ITextComponent>()
        val data = mutableListOf<BlockData>()

        for (j in 0 until context.multiblock.size.y) {
            for (i in 0 until context.multiblock.size.x) {
                for (k in 0 until context.multiblock.size.z) {
                    val pos = context.facing.rotatePoint(context.multiblock.center, BlockPos(i, j, k)).subtract(context.multiblock.center)
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

    fun activateMultiblockStructure(context: MultiblockContext) {
        val data = mutableListOf<BlockData>()

        for (j in 0 until context.multiblock.size.y) {
            for (i in 0 until context.multiblock.size.x) {
                for (k in 0 until context.multiblock.size.z) {
                    val pos = context.facing.rotatePoint(context.multiblock.center, BlockPos(i, j, k)).subtract(context.multiblock.center)
                    val comp = context.multiblock.scheme[i, j, k]
                    comp.activateBlock(pos, context)
                    data.add(comp.getBlockData(pos, context))
                }
            }
        }
        context.multiblock.onActivate(data, context)
    }

    fun deactivateMultiblockStructure(context: MultiblockContext) {
        val data = mutableListOf<BlockData>()

        for (j in 0 until context.multiblock.size.y) {
            for (i in 0 until context.multiblock.size.x) {
                for (k in 0 until context.multiblock.size.z) {
                    val pos = context.facing.rotatePoint(context.multiblock.center, BlockPos(i, j, k)).subtract(context.multiblock.center)
                    val comp = context.multiblock.scheme[i, j, k]
                    comp.deactivateBlock(pos, context)
                    data.add(comp.getBlockData(pos, context))
                }
            }
        }
        context.multiblock.onDeactivate(data, context)
    }
}