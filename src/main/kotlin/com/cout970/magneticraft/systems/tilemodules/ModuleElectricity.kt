package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.core.INode
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.getTileEntitiesIn
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.tileentity.tryConnect
import com.cout970.magneticraft.misc.vector.length
import com.cout970.magneticraft.misc.vector.minus
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.gui.DATA_ID_VOLTAGE_LIST
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/06/29.
 */
class ModuleElectricity(
    val electricNodes: List<IElectricNode>,
    val canConnectAtSide: (EnumFacing?) -> Boolean = { true },
    val onWireChange: (EnumFacing?) -> Unit = {},
    val onUpdateConnections: (ModuleElectricity) -> Unit = {},
    val maxWireDistance: Double = 16.0,
    val connectableDirections: () -> List<Pair<BlockPos, EnumFacing>> = { NEGATIVE_DIRECTIONS.map { it.toBlockPos() to it.opposite } },
    val capabilityFilter: (EnumFacing?) -> Boolean = { true },
    override val name: String = "module_electricity"
) : IModule, IElectricNodeHandler {

    companion object {
        @JvmStatic
        val NEGATIVE_DIRECTIONS = EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }
    }

    override lateinit var container: IModuleContainer

    var autoConnectWires = false

    val inputNormalConnections = mutableListOf<IElectricConnection>()
    val outputNormalConnections = mutableListOf<IElectricConnection>()

    val inputWiredConnections = mutableListOf<IElectricConnection>()
    val outputWiredConnections = mutableListOf<IElectricConnection>()

    val unloadedWireConnections = mutableListOf<Pair<String, NodeID>>()

    override fun update() {
        updateConnections()
        iterate()
    }

    fun updateConnections() {
        if (unloadedWireConnections.isNotEmpty()) {
            loadConnections()
        }

        if (container.shouldTick(40)) {
            updateNormalConnections()
            if (electricNodes.any { it is IWireConnector }) {
                updateWiredConnections()
            }
            onUpdateConnections(this)
        }

        if (world.isServer && container.shouldTick(400) && electricNodes.any { it is IWireConnector }) {
            container.sendUpdateToNearPlayers()
        }
    }

    fun loadConnections() {
        val iter = unloadedWireConnections.iterator()
        while (iter.hasNext()) {
            val (localName, nodeID) = iter.next()

            val thisNode = electricNodes.find { it.id.name == localName } ?: continue

            val otherTile = world.getTileEntity(nodeID.pos) ?: continue
            val other = otherTile.getOrNull(ELECTRIC_NODE_HANDLER, null) ?: continue
            val otherNode = (other.getNode(nodeID) as? IElectricNode) ?: continue

            tryConnect(this, thisNode, other, otherNode, null)
            iter.remove()
        }
    }

    fun updateNormalConnections() {
        clearNormalConnections()
        if (world.isClient) return

        electricNodes.forEach { thisNode ->

            val conDir = connectableDirections()
            val spots = conDir.mapNotNull { (vec, side) ->
                val tile = world.getTileEntity(pos.add(vec)) ?: return@mapNotNull null
                tile to side
            }
            val handlers = spots.mapNotNull { (tile, side) ->
                val handler = tile.getOrNull(ELECTRIC_NODE_HANDLER, side)
                if (handler === null || handler === this) return@mapNotNull null
                handler to side
            }

            for ((handler, side) in handlers) {
                val electricNodes = handler.nodes
                    .filter { it is IElectricNode }
                    .map { it as IElectricNode }

                electricNodes.forEach { otherNode ->
                    tryConnect(this, thisNode, handler, otherNode, side.opposite)
                }
            }
        }
    }

    fun updateWiredConnections() {
        if (autoConnectWires) {
            val size = Vec3i(16, 5, 16)
            autoConnectWires(pos - size, pos + size)
        }

        val changed = outputWiredConnections.removeAll {
            // Chunk unloaded, we do not remove the connection
            it.secondNode.world.getTileEntity(it.secondNode.pos) ?: return@removeAll false
            val handler = getHandler(it.secondNode)
            handler == null || handler.nodes.none { node -> node.id == it.secondNode.id }
        }

        if (changed || autoConnectWires) {
            onWireChange(null)
        }
    }

    fun autoConnectWires(start: BlockPos, end: BlockPos) {
        val thisNode = electricNodes.find { it is IWireConnector } as? IWireConnector ?: return

        val filter: (TileEntity) -> Boolean = { it != container.tile }
        val handlers = world.getTileEntitiesIn(start, end, filter).mapNotNull {
            it.getOrNull(ELECTRIC_NODE_HANDLER, null)
        }

        handlers.forEach { handler ->
            handler.nodes
                .filterIsInstance<IWireConnector>()
                .filter { it.connectorsSize == thisNode.connectorsSize }
                .forEach { otherNode ->
                    if (outputWiredConnections.size < 16) {
                        tryConnect(this, thisNode, handler, otherNode, null)
                    }
                }
        }
    }

    fun iterate() {
        outputNormalConnections.forEach(IElectricConnection::iterate)
        outputWiredConnections.forEach(IElectricConnection::iterate)
    }

    override fun canConnect(thisNode: IElectricNode, other: IElectricNodeHandler, otherNode: IElectricNode,
                            side: EnumFacing?): Boolean = defaultCanConnectImpl(thisNode, other, otherNode, side)

    fun defaultCanConnectImpl(thisNode: IElectricNode, other: IElectricNodeHandler, otherNode: IElectricNode,
                              side: EnumFacing?): Boolean {
        if (other === this || otherNode === thisNode) return false
        if (!canConnectAtSide(side)) return false
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
        val list: MutableList<IElectricConnection> = if (side == null) {
            if (output) outputWiredConnections else inputWiredConnections
        } else {
            if (output) outputNormalConnections else inputNormalConnections
        }
        list.add(connection)
        onWireChange(side)
        container.markDirty()
    }

    override fun removeConnection(connection: IElectricConnection?) {
        inputNormalConnections.remove(connection)
        outputNormalConnections.remove(connection)
        inputWiredConnections.remove(connection)
        outputWiredConnections.remove(connection)
        onWireChange(null)
    }

    override fun onBreak() {
        clearNormalConnections()
        clearWireConnections()

        inputNormalConnections.removeAll { con ->
            val handler = getHandler(con.firstNode)
            handler?.removeConnection(con)
            true
        }
        inputWiredConnections.removeAll { con ->
            val handler = getHandler(con.firstNode)
            handler?.removeConnection(con)
            true
        }
        onWireChange(null)
    }

    fun clearNormalConnections() {
        outputNormalConnections.removeAll { con ->
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            true
        }
    }

    fun clearWireConnections() {
        outputWiredConnections.removeAll { con ->
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            true
        }
        onWireChange(null)
    }

    fun getHandler(node: IElectricNode): IElectricNodeHandler? {
        val tile = node.world.getTileEntity(node.pos) ?: return null
        return tile.getOrNull(ELECTRIC_NODE_HANDLER, null) ?: return null
    }

    override fun getNodes(): MutableList<INode> = electricNodes.toMutableList()

    override fun getNode(id: NodeID): INode? = electricNodes.find { it.id == id }

    override fun getRef(): ITileRef = container.ref

    override fun getInputConnections(): MutableList<IElectricConnection> = inputNormalConnections with inputWiredConnections

    override fun getOutputConnections(): MutableList<IElectricConnection> = outputNormalConnections with outputWiredConnections

    override fun hasCapability(cap: Capability<*>, facing: EnumFacing?): Boolean {
        return cap == ELECTRIC_NODE_HANDLER && capabilityFilter.invoke(facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if (!capabilityFilter.invoke(facing)) return null
        return this as T
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        autoConnectWires = nbt.getBoolean("auto_connect")
        nbt.readList("ElectricNodes") { nodeList ->
            nodes.forEachIndexed { index, node ->
                node.deserializeNBT(nodeList.getTagCompound(index))
            }
        }
        nbt.readList("ElectricConnections") { connectionList ->
            val unloaded = mutableListOf<Pair<String, NodeID>>()
            connectionList.forEachTag { tag ->
                unloaded += tag.getString("first") to NodeID.deserializeFromNBT(tag.getCompoundTag("second"))
            }
            unloadedWireConnections.addAll(unloaded)
        }
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("auto_connect", autoConnectWires)
        list("ElectricNodes") {
            nodes.forEach { appendTag(it.serializeNBT()) }
        }
        list("ElectricConnections") {
            outputWiredConnections.forEach {
                newNbt {
                    add("first", it.firstNode.id.name)
                    add("second", it.secondNode.id.serializeNBT())
                }
            }
            unloadedWireConnections.forEach {
                newNbt {
                    add("first", it.first)
                    add("second", it.second.serializeNBT())
                }
            }
        }
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return electricNodes
            .filterIsInstance<ElectricNode>()
            .mapIndexed { index, node ->
                FloatSyncVariable(
                    id = DATA_ID_VOLTAGE_LIST[index],
                    getter = { node.voltage.toFloat() },
                    setter = { node.voltage = it.toDouble() }
                )
            }
    }
}