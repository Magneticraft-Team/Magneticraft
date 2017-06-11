package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.internal.energy.ElectricConnection
import com.cout970.magneticraft.misc.energy.UnloadedElectricConnection
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.vector.length
import com.cout970.magneticraft.util.vector.minus
import com.cout970.magneticraft.util.vector.plus
import com.google.common.base.Predicate
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/02/26.
 */

class TraitElectricity(
        tile: TileBase,
        //all connectors
        val electricNodes: List<IElectricNode>,
        val canConnectAtSideImpl: (EnumFacing?) -> Boolean = { true },
        val onWireChangeImpl: (EnumFacing?) -> Unit = {},
        val onUpdateConnections: () -> Unit = {},
        val canConnectImpl: (TraitElectricity, IElectricNode, IElectricNodeHandler, IElectricNode, EnumFacing?) -> Boolean = TraitElectricity::defaultCanConnectImpl,
        val maxWireDistance: Double = 16.0

) : TileTrait(tile), IElectricNodeHandler {

    val inputNormalConnections = mutableListOf<IElectricConnection>()
    val outputNormalConnections = mutableListOf<IElectricConnection>()

    val inputWiredConnections = mutableListOf<IElectricConnection>()
    val outputWiredConnections = mutableListOf<IElectricConnection>()

    val unloadedConnections = mutableListOf<UnloadedElectricConnection>()

    private var firstTicks = 0
    var autoConnectWires = false

    override fun update() {
        if (tile.shouldTick(40)) {
            updateConnections()
        }

        resolveUnloadedConnections()

        if (world.isServer) {
            //update to sync connections every 20 seconds
            if (tile.shouldTick(400)) {
                tile.sendUpdateToNearPlayers()
            }
            // when the world is loaded the player take some ticks to
            // load so this wait for the player to send an update
            if (firstTicks > 0) {
                firstTicks--
                if (firstTicks % 20 == 0) {
                    tile.sendUpdateToNearPlayers()
                }
            }
            iterate()
        }
    }

    fun iterate() {
        outputNormalConnections.forEach(IElectricConnection::iterate)
        outputWiredConnections.forEach(IElectricConnection::iterate)
    }

    fun resolveUnloadedConnections() {
        if (!unloadedConnections.isEmpty()) {
            val iterator = unloadedConnections.iterator()
            while (iterator.hasNext()) {
                val con = iterator.next()
                if (con.create(world, this)) {
                    iterator.remove()
                }
            }
            if (world.isServer) {
                firstTicks = 60
            } else {
                onWireChange(null)
            }
        }
    }

    fun updateConnections() {
        clearNormalConnections()
        for (thisNode in nodes.filter { it is IElectricNode }.map { it as IElectricNode }) {
            loop@ for (dir in NEGATIVE_DIRECTIONS) {
                val tile = world.getTileEntity(thisNode.pos.offset(dir)) ?: continue@loop
                if (tile === this.tile) continue@loop
                val handler = ELECTRIC_NODE_HANDLER!!.fromTile(tile,
                        dir.opposite) as? IElectricNodeHandler ?: continue@loop
                for (otherNode in handler.nodes.filter { it is IElectricNode }.map { it as IElectricNode }) {
                    if (this.canConnect(thisNode, handler, otherNode, dir) && handler.canConnect(otherNode, this,
                            thisNode, dir.opposite)) {
                        val connection = ElectricConnection(thisNode, otherNode)
                        this.addConnection(connection, dir, true)
                        handler.addConnection(connection, dir.opposite, false)
                    }
                }
            }
        }
        updateWiredConnections()
    }

    fun updateWiredConnections() {
        onUpdateConnections()
        if (autoConnectWires) {
            val size = Vec3i(16, 5, 16)
            autoConnectWires(tile, this, world, pos - size, pos + size)
        }
        onWireChange(null)
        //remove invalid nodes in wiredConnections
        val iterator = outputWiredConnections.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            val handler = getHandler(i.secondNode)
            if (handler == null || !handler.nodes.contains(i.secondNode)) {
                iterator.remove()
                continue
            }
        }
    }

    fun clearNormalConnections() {
        val iterator = outputNormalConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    fun clearWireConnections() {
        val iterator = outputWiredConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    override fun canConnect(thisNode: IElectricNode, other: IElectricNodeHandler, otherNode: IElectricNode,
                            side: EnumFacing?): Boolean = canConnectImpl(this, thisNode, other, otherNode, side)

    fun defaultCanConnectImpl(thisNode: IElectricNode, other: IElectricNodeHandler, otherNode: IElectricNode,
                              side: EnumFacing?): Boolean {
        if (other == this || otherNode == thisNode) return false
        if (!canConnectAtSideImpl(side)) return false
        if (side == null) {
            if (thisNode !is IWireConnector || otherNode !is IWireConnector) return false
            if (thisNode.connectorsSize != otherNode.connectorsSize) return false

            if (inputWiredConnections.any { it.firstNode == otherNode }) {
                return false
            }
            if (outputWiredConnections.any { it.secondNode == otherNode }) {
                return false
            }
            val distance = (thisNode.pos - otherNode.pos).length
            if (distance > maxWireDistance) {
                return false
            }
        }
        return true
    }

    override fun addConnection(connection: IElectricConnection, side: EnumFacing?, output: Boolean) {
        if (side == null) {
            if (output) {
                outputWiredConnections.add(connection)
            } else {
                inputWiredConnections.add(connection)
            }
        } else {
            if (output) {
                outputNormalConnections.add(connection)
            } else {
                inputNormalConnections.add(connection)
            }
        }
        onWireChange(side)
    }

    override fun removeConnection(connection: IElectricConnection) {
        inputNormalConnections.remove(connection)
        outputNormalConnections.remove(connection)
        inputWiredConnections.remove(connection)
        outputWiredConnections.remove(connection)
        onWireChange(null)
    }

    fun onWireChange(side: EnumFacing?) = onWireChangeImpl(side)

    override fun onBreak() {
        super.onBreak()
        clearNormalConnections()
        clearWireConnections()
        this.let {
            val iterator = inputNormalConnections.iterator()
            while (iterator.hasNext()) {
                val con = iterator.next()
                val handler = getHandler(con.firstNode)
                handler?.removeConnection(con)
                iterator.remove()
            }
        }
        val iterator = inputWiredConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val handler = getHandler(con.firstNode)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    override fun getNodes(): MutableList<INode> = electricNodes.toMutableList()

    override fun getInputConnections(): MutableList<IElectricConnection> {
        return inputNormalConnections with inputWiredConnections
    }

    override fun getOutputConnections(): MutableList<IElectricConnection> {
        return outputNormalConnections with outputWiredConnections
    }

    override fun deserialize(nbt: NBTTagCompound) {
        nbt.readList("ElectricNodes") { nodeList ->
            nodes.forEachIndexed { index, node ->
                node.deserializeNBT(nodeList.getTagCompound(index))
            }
        }
        nbt.readList("ElectricConnections") { connectionList ->
            connectionList.forEach {
                unloadedConnections += UnloadedElectricConnection.load(it)
            }
        }
    }

    override fun serialize(): NBTTagCompound? {
        return newNbt {
            list("ElectricNodes") {
                nodes.forEach {
                    appendTag(it.serializeNBT())
                }
            }
            list("ElectricConnections") {
                for (i in outputWiredConnections) {
                    appendTag(UnloadedElectricConnection.save(i))
                }
                unloadedConnections
                        .filter { it.isValid }
                        .forEach {
                            appendTag(UnloadedElectricConnection.save(it))
                        }
            }
        }
    }

    override fun hasCapability(capability: Capability<*>, enumFacing: EnumFacing?): Boolean {
        return capability == ELECTRIC_NODE_HANDLER
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>, enumFacing: EnumFacing?): T {
        return this as T
    }

    companion object {

        val NEGATIVE_DIRECTIONS = EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }

        fun autoConnectWires(tile: TileEntity, thisHandler: IElectricNodeHandler, world: World, start: BlockPos,
                             end: BlockPos, filter: (IWireConnector, IWireConnector) -> Boolean = { _, _ -> true }) {
            val predicate: Predicate<TileEntity> = Predicate { it != tile }
            getTileEntitiesIn(world, start, end, predicate)
                    .map { ELECTRIC_NODE_HANDLER!!.fromTile(it) }
                    .filterIsInstance<IElectricNodeHandler>()
                    .forEach { connectHandlers(thisHandler, it, filter) }
        }

        fun connectHandlers(first: IElectricNodeHandler, second: IElectricNodeHandler,
                            filter: (IWireConnector, IWireConnector) -> Boolean = { _, _ -> true }): Boolean {
            var result = false
            for (firstNode in first.nodes.filter { it is IWireConnector }.map { it as IWireConnector }) {
                second.nodes
                        .filterIsInstance<IWireConnector>()
                        .filter { filter.invoke(firstNode, it) && connectNodes(first, firstNode, second, it) }
                        .forEach { result = true }
            }
            return result
        }

        fun connectNodes(first: IElectricNodeHandler, firstNode: IWireConnector, second: IElectricNodeHandler,
                         secondNode: IWireConnector): Boolean {
            if (first.canConnect(firstNode, second, secondNode, null) && second.canConnect(secondNode, first, firstNode,
                    null)) {
                val connection = ElectricConnection(firstNode, secondNode)
                first.addConnection(connection, null, true)
                second.addConnection(connection, null, false)
                return true
            }
            return false
        }

        @Suppress("LoopToCallChain")
        fun getTileEntitiesIn(world: World, start: BlockPos, end: BlockPos,
                              filter: Predicate<TileEntity>): List<TileEntity> {
            val list = mutableListOf<TileEntity>()
            for (x in start.x..end.x step 16) {
                for (z in start.z..end.z step 16) {
                    val chunk = world.getChunkFromChunkCoords(x shr 4, z shr 4)
                    for ((pos, tile) in chunk.tileEntityMap) {
                        if (!tile.isInvalid && pos in (start to end) && filter.apply(tile)) {
                            list.add(tile)
                        }
                    }
                }
            }
            return list
        }

        fun getHandler(node: IElectricNode): IElectricNodeHandler? {
            val tile = node.world.getTileEntity(node.pos)
            if (tile != null) {
                val handler = ELECTRIC_NODE_HANDLER!!.fromTile(tile)
                if (handler is IElectricNodeHandler) {
                    return handler
                }
            }
            return null
        }

        operator fun Pair<BlockPos, BlockPos>.contains(pos: BlockPos): Boolean {
            return pos.x >= first.x && pos.x <= second.x &&
                   pos.y >= first.y && pos.y <= second.y &&
                   pos.z >= first.z && pos.z <= second.z
        }
    }
}