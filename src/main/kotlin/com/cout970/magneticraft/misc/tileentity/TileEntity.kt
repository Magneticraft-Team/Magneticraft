package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.heat.IHeatNodeHandler
import com.cout970.magneticraft.api.internal.energy.ElectricConnection
import com.cout970.magneticraft.api.internal.heat.HeatConnection
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tileentities.TileBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

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

fun <T> IBlockAccess.getCap(cap: Capability<T>?, pos: BlockPos, side: EnumFacing?): T? {
    val tile = getTileEntity(pos) ?: return null
    return cap?.fromTile(tile, side)
}

inline fun <reified T> TileBase.getModule(): T? = container.modules.find { it is T } as? T

inline fun <reified T> IBlockAccess.getModule(pos: BlockPos): T? {
    val tile = getTile<TileBase>(pos)
    return tile?.getModule<T>()
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
               otherHandler: IElectricNodeHandler, otherNode: IElectricNode, side: EnumFacing?): Boolean {

    if (canConnect(thisHandler, thisNode, otherHandler, otherNode, side)) {
        val connection = ElectricConnection(thisNode, otherNode)
        thisHandler.addConnection(connection, side, true)
        otherHandler.addConnection(connection, side?.opposite, false)
        return true
    }

    return false
}

fun canConnect(thisHandler: IElectricNodeHandler, thisNode: IElectricNode,
               otherHandler: IElectricNodeHandler, otherNode: IElectricNode, side: EnumFacing?): Boolean {

    return thisHandler.canConnect(thisNode, otherHandler, otherNode, side) &&
        otherHandler.canConnect(otherNode, thisHandler, thisNode, side?.opposite)
}

fun tryConnect(thisHandler: IHeatNodeHandler, thisNode: IHeatNode,
               otherHandler: IHeatNodeHandler, otherNode: IHeatNode, side: EnumFacing) {

    if (canConnect(thisHandler, thisNode, otherHandler, otherNode, side)) {
        val connection = HeatConnection(thisNode, otherNode)
        thisHandler.addConnection(connection, side, true)
        otherHandler.addConnection(connection, side.opposite, false)
    }
}

fun canConnect(thisHandler: IHeatNodeHandler, thisNode: IHeatNode,
               otherHandler: IHeatNodeHandler, otherNode: IHeatNode, side: EnumFacing): Boolean {

    return thisHandler.canConnect(thisNode, otherHandler, otherNode, side) &&
        otherHandler.canConnect(otherNode, thisHandler, thisNode, side.opposite)
}