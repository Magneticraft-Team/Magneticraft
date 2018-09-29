package com.cout970.magneticraft.systems.tilemodules.pipe

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.vector.plus
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by cout970 on 2017/08/28.
 */

interface IPathFindingNode {
    val ref: ITileRef
    val sides: List<EnumFacing>
}

class NearestFirstSearch(start: IPathFindingNode, val getter: InspectFunc) {

    private val scanned = linkedSetOf<BlockPos>()
    private val toScanSet = linkedSetOf<BlockPos>()
    private val toScanList = LinkedList<Pair<BlockPos, EnumFacing>>()
    private val resultList = linkedSetOf<IPathFindingNode>()

    val world: World = start.ref.world

    init {
        scanned.add(start.ref.pos)
        resultList.add(start)
        start.let {
            resultList.add(it)
            it.sides.forEach { side ->
                toScanList.add(it.ref.pos + side to side.opposite)
                toScanSet.add(it.ref.pos)
            }
        }
    }

    fun iterate(): Boolean {
        if (toScanSet.isEmpty()) return false
        if (toScanList.isEmpty()) return false

        val (pos, side) = toScanList.pop()
        toScanSet.remove(pos)
        scanned.add(pos)

        val tile = world.getTileEntity(pos) ?: return true
        val next = getter(tile, side)

        next.forEach {
            resultList.add(it)
            it.sides.forEach { side ->
                val blockPos = it.ref.pos + side
                if (blockPos !in scanned && blockPos !in toScanSet) {
                    toScanList.add(blockPos to side.opposite)
                    toScanSet.add(blockPos)
                }
            }
        }
        return true
    }

    fun getResult() = resultList.toList()
}

fun nearestFirstSearch(start: IPathFindingNode, getter: InspectFunc)
    : List<IPathFindingNode> {

    val search = NearestFirstSearch(start, getter)

    while (search.iterate());

    return search.getResult()
}
