package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.internal.energy.ElectricConnection
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/20.
 */

fun World.shouldTick(pos: BlockPos, time: Int): Boolean {
    return (totalWorldTime + pos.hashCode()) % time == 0L
}

fun IModuleContainer.shouldTick(ticks: Int): Boolean {
    return (world.totalWorldTime + pos.hashCode()) % ticks == 0L
}

inline fun <reified T : TileEntity> World.getTile(pos: BlockPos): T? {
    val tile = getTileEntity(pos)
    return tile as? T
}

inline fun <reified T : TileEntity> IBlockAccess.getTile(pos: BlockPos): T? {
    val tile = getTileEntity(pos)
    return tile as? T
}

operator fun Pair<BlockPos, BlockPos>.contains(pos: BlockPos): Boolean {
    return pos.x >= first.x && pos.x <= second.x &&
           pos.y >= first.y && pos.y <= second.y &&
           pos.z >= first.z && pos.z <= second.z
}

@Suppress("LoopToCallChain")
fun World.getTileEntitiesIn(start: BlockPos, end: BlockPos,
                            filter: (TileEntity) -> Boolean = { true }): List<TileEntity> {

    val list = mutableListOf<TileEntity>()
    for (x in start.x..end.x step 16) {
        for (z in start.z..end.z step 16) {

            val chunk = getChunkFromChunkCoords(x shr 4, z shr 4)

            for ((pos, tile) in chunk.tileEntityMap) {
                if (!tile.isInvalid && pos in (start to end) && filter.invoke(tile)) {
                    list.add(tile)
                }
            }
        }
    }
    return list
}

fun tryConnect(thisHandler: IElectricNodeHandler, thisNode: IElectricNode,
               otherHandler: IElectricNodeHandler, otherNode: IElectricNode, side: EnumFacing?) {

    if (thisHandler.canConnect(thisNode, otherHandler, otherNode, side) &&
        otherHandler.canConnect(otherNode, thisHandler, thisNode, side?.opposite)) {

        val connection = ElectricConnection(thisNode, otherNode)
        thisHandler.addConnection(connection, side, true)
        otherHandler.addConnection(connection, side?.opposite, false)
    }
}