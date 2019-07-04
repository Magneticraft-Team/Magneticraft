package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.core.INode
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.heat.IHeatNodeHandler
import com.cout970.magneticraft.api.internal.heat.HeatNode
import com.cout970.magneticraft.misc.getTagCompound
import com.cout970.magneticraft.misc.list
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.readList
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.tileentity.tryConnect
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.gui.DATA_ID_HEAT_LIST
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/06/29.
 */
class ModuleHeat(
    vararg val heatNodes: IHeatNode,
    val canConnect: (ModuleHeat, IHeatNode, IHeatNodeHandler, IHeatNode, EnumFacing?) -> Boolean = ModuleHeat::defaultCanConnectImpl,
    val canConnectAtSide: (EnumFacing?) -> Boolean = { true },
    val onUpdateConnections: (ModuleHeat) -> Unit = {},
    val capabilityFilter: (EnumFacing?) -> Boolean = { true },
    val connectableDirections: () -> List<Pair<BlockPos, EnumFacing>> = {
        NEGATIVE_DIRECTIONS.filter { canConnectAtSide(it) }.map { it.toBlockPos() to it.opposite }
    },
    override val name: String = "module_heat"
) : IModule, IHeatNodeHandler {

    companion object {
        @JvmStatic
        val NEGATIVE_DIRECTIONS = EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }
    }

    override lateinit var container: IModuleContainer

    val inputNormalConnections = mutableListOf<IHeatConnection>()
    val outputNormalConnections = mutableListOf<IHeatConnection>()

    override fun update() {
        if (world.isClient) return
        updateConnections()
        iterate()
    }

    fun updateConnections() {

        if (container.shouldTick(40)) {
            updateNormalConnections()
            onUpdateConnections(this)
            container.sendUpdateToNearPlayers()
        }
    }

    fun updateNormalConnections() {
        clearNormalConnections()
        heatNodes.forEach { thisNode ->

            val conDir = connectableDirections()
            val spots = conDir.mapNotNull { (vec, side) ->
                val tile = world.getTileEntity(pos.add(vec)) ?: return@mapNotNull null
                tile to side
            }
            val handlers = spots.mapNotNull { (tile, side) ->
                val handler = tile.getOrNull(HEAT_NODE_HANDLER, side)
                if (handler === null || handler === this) return@mapNotNull null
                handler to side
            }

            for ((handler, side) in handlers) {
                val heatNodes = handler.nodes.filterIsInstance<IHeatNode>()

                heatNodes.forEach { otherNode ->
                    tryConnect(this, thisNode, handler, otherNode, side.opposite)
                }
            }
        }
    }

    fun iterate() {
        outputNormalConnections.forEach(IHeatConnection::iterate)
    }

    override fun canConnect(thisNode: IHeatNode, other: IHeatNodeHandler, otherNode: IHeatNode,
                            side: EnumFacing): Boolean = canConnect(this, thisNode, other, otherNode, side)

    fun defaultCanConnectImpl(thisNode: IHeatNode, other: IHeatNodeHandler, otherNode: IHeatNode,
                              side: EnumFacing?): Boolean {
        if (other == this || otherNode == thisNode || side == null) return false
        return canConnectAtSide(side)
    }

    override fun addConnection(connection: IHeatConnection, side: EnumFacing, output: Boolean) {
        val list = if (output) outputNormalConnections else inputNormalConnections
        list.add(connection)
    }

    override fun removeConnection(connection: IHeatConnection?) {
        inputNormalConnections.remove(connection)
        outputNormalConnections.remove(connection)
    }

    override fun onBreak() {
        clearNormalConnections()
        inputNormalConnections.removeAll { con ->
            val handler = getHandler(con.firstNode)
            handler?.removeConnection(con)
            true
        }
    }

    fun clearNormalConnections() {
        outputNormalConnections.removeAll { con ->
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            true
        }
    }

    fun getHandler(node: IHeatNode): IHeatNodeHandler? {
        val tile = node.world.getTileEntity(node.pos) ?: return null
        return tile.getOrNull(HEAT_NODE_HANDLER, null) ?: return null
    }

    override fun getNodes(): MutableList<INode> = heatNodes.toMutableList()

    override fun getNode(id: NodeID): INode? = heatNodes.find { it.id == id }

    override fun getRef(): ITileRef = container.ref

    override fun getInputConnections(): MutableList<IHeatConnection> = inputNormalConnections

    override fun getOutputConnections(): MutableList<IHeatConnection> = outputNormalConnections

    override fun hasCapability(cap: Capability<*>, facing: EnumFacing?): Boolean {
        return cap == HEAT_NODE_HANDLER && capabilityFilter.invoke(facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if (cap != HEAT_NODE_HANDLER) return null
        if (!capabilityFilter.invoke(facing)) return null
        return this as T
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        nbt.readList("HeatNodes") { nodeList ->
            nodes.forEachIndexed { index, node ->
                node.deserializeNBT(nodeList.getTagCompound(index))
            }
        }
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        list("HeatNodes") {
            nodes.forEach { appendTag(it.serializeNBT()) }
        }
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return heatNodes
            .filterIsInstance<HeatNode>()
            .mapIndexed { index, node ->
                FloatSyncVariable(
                    id = DATA_ID_HEAT_LIST[index],
                    getter = { node.internalEnergy.toFloat() },
                    setter = { node.internalEnergy = it.toDouble() }
                )
            }
    }
}