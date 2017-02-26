package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatConnection
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/02/21.
 */
class HeatHandler(
        tile: TileBase,
        val heatNodes: List<IHeatNode>,
        val updateConnectionsImpl: (HeatHandler) -> Unit = HeatHandler.Companion::updateHeatConnections
) : TileTrait(tile), IHeatHandler {

    private val connections: MutableList<IHeatConnection> = mutableListOf()
    private var initiated = false
    var lightLevel = 0.0f
        private set

    override fun getNodes(): MutableList<INode> = heatNodes.toMutableList()
    override fun getConnections(): MutableList<IHeatConnection> = connections.toMutableList()

    fun iterate() {

        if (tile.shouldTick(UPDATE_LIGHT_DELAY)) {
            var mix = 0f
            heatNodes.forEach {
                mix += interpolate(it.temperature, MIN_EMISSION_TEMP, MAX_EMISSION_TEMP).toFloat()
            }
            val lightLevel = mix / heatNodes.size

            if (this.lightLevel != lightLevel) {
                this.lightLevel = lightLevel
                //TODO  is this unfinished stuff or just Diogenes syndrome?
                //world.getBlock<BlockBase>(pos)?.setLightLevel(lightLevel)
                //sendUpdateToNearPlayers()
            }
        }
        if (world.isServer) {
            heatNodes.forEach(IHeatNode::iterate)
            connections.forEach(IHeatConnection::iterate)
            if (tile.shouldTick(400)) {
                updateConnections()
                tile.sendUpdateToNearPlayers()
            }
        }
    }

    fun updateConnections() = updateConnectionsImpl(this)

    override fun addConnection(connection: IHeatConnection) {
        if (connection !in connections) {
            connections += connection
        }
    }

    override fun removeConnection(connection: IHeatConnection) {
        connections -= connection
    }

    override fun onFullyLoad() {
        if (!initiated) {
            updateConnections()
            initiated = true
        }
    }

    override fun onBreak() {
        for (i in EnumFacing.values()) {
            val tileOther = world.getTileEntity(pos.offset(i)) ?: continue
            val handler = (NODE_HANDLER!!.fromTile(tileOther, i) ?: continue) as? IHeatHandler ?: continue
            for (otherNode in heatNodes) {
                connections.forEach { connection ->
                    handler.removeConnection(connection)
                }
            }
        }
        connections.clear()
    }

    override fun deserialize(nbt: NBTTagCompound) {
        if (nbt.hasKey("HeatNodes")) {
            val list = nbt.getList("HeatNodes")
            heatNodes.forEachIndexed { index, node ->
                node.deserializeNBT(list.getTagCompound(index))
            }
        }
        if (nbt.hasKey("LightLevelCache")) {
            lightLevel = nbt.getFloat("LightLevelCache")
        }
    }

    override fun serialize(): NBTTagCompound? {
        return newNbt {
            add("LightLevelCache", lightLevel)
            list("HeatNodes") {
                heatNodes.forEach { node ->
                    node?.apply {
                        appendTag(serializeNBT())
                    }
                }
            }
        }
    }

    fun getComparatorOutput(): Int {
        return (Math.floor(heatNodes[0].temperature / heatNodes[0].maxTemperature) * 15).toInt()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == NODE_HANDLER) return this as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == NODE_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    companion object {
        val UPDATE_LIGHT_DELAY = 20


        private fun updateHeatConnections(handler_: HeatHandler) {
            handler_.apply {
                for (node in heatNodes) {
                    for (side in EnumFacing.values()) {
                        val tileOther = world.getTileEntity(pos.offset(side)) ?: continue
                        val handler = (NODE_HANDLER!!.fromTile(tileOther,
                                side) ?: continue) as? IHeatHandler ?: continue
                        val heatNodes = handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }

                        for (otherNode in heatNodes) {
                            this.addConnection(HeatConnection(node, otherNode))
                            handler.addConnection(HeatConnection(otherNode, node))
                        }
                    }
                }
            }
        }
    }
}