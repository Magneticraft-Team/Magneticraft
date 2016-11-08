package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.HeatConnection
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.util.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileElectricHeatBase : TileElectricBase(), IHeatHandler {

    abstract val heatNodes: List<IHeatNode>
    val heatConnections: MutableSet<IHeatConnection> = mutableSetOf()
    val lightLevelUpdateDelay = 20
    var lightLevelCache = 0.0f
    var initiated = false

    override fun update() {
        if (shouldTick(20)) {
            if (!worldObj.isRemote) {
                heatNodes.forEach { it.updateHeat() }
                heatConnections.forEach { it.iterate() }
            }
        }
        if (shouldTick(lightLevelUpdateDelay)) {
            heatNodes.forEach {
                if (it.emit) {
                    val lightLevel: Float = interpolate(it.temperature, MIN_EMISSION_TEMP, MAX_EMISSION_TEMP).toFloat()
                    if (lightLevelCache != lightLevel) {
                        lightLevelCache = lightLevel
                        //world.getBlock<BlockBase>(pos)?.setLightLevel(lightLevel)
                        //sendUpdateToNearPlayers()
                    }
                }
            }
        }
        if (world.isServer) {
            //update to sync connections every 20 seconds
            if (shouldTick(400)) {
                updateHeatConnections()
            }
        }
        super.update()
    }

    override fun onLoad() {
        super.onLoad()
        if (initiated == false) {
            updateHeatConnections()
            for (i in heatNodes) i.heat = (biomeTemptoKelvin(world, pos) * i.specificHeat).toLong()
            initiated = true
        }
    }

    override fun updateHeatConnections() {
        for (i in heatNodes) {
            for (j in EnumFacing.values()) {
                val tileOther = world.getTileEntity(pos.offset(j)) ?: continue
                if (tileOther === this) continue
                val handler = NODE_HANDLER!!.fromTile(tileOther, j) ?: continue
                if (handler !is IHeatHandler) continue
                for (otherNode in handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }) {
                    heatConnections.add(HeatConnection(i, otherNode))
                    handler.addConnection(HeatConnection(otherNode, i))
                }
            }
            i.setAmbientTemp(biomeTemptoKelvin(world, pos)) //This might be unnecessary
        }
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("HeatNodes")) {
            val tag = compound.getCompoundTag("HeatNodes")
            for (i in 0 until heatNodes.size) {
                heatNodes[i].deserializeNBT(tag.getCompoundTag("HeatNode" + i))
            }
        }
        if (compound.hasKey("HeatConnections")) {
            val tag = compound.getCompoundTag("HeatConnections")
            for (i in 0 until heatNodes.size) {
                heatNodes[i].deserializeNBT(tag.getCompoundTag("HeatConnection" + i))
            }
        }
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        val tag = NBTTagCompound()
        for (i in 0 until heatNodes.size) {
            tag.setTag("HeatNode" + i, heatNodes[i].serializeNBT())
        }
        compound!!.setTag("HeatNodes", tag)
        return super.writeToNBT(compound)
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setFloat("lightLevelCache", lightLevelCache)
    }

    override fun load(nbt: NBTTagCompound) {
        lightLevelCache = nbt.getFloat("lightLevelCache")
        initiated = true
    }

    override fun addConnection(connection: IHeatConnection) {
        heatConnections.add(connection)
    }

    override fun getNodes(): List<INode> = heatNodes + electricNodes

    override fun removeConnection(connection: IHeatConnection) {
        heatConnections.remove(connection)
    }

    override fun removeConnection(node: IHeatNode) {
        for (j in heatConnections) {
            if (j.firstNode === node || j.secondNode === node)
                heatConnections.remove(j)
        }
    }

    override fun getConnections(): List<IHeatConnection> {
        return heatConnections.toList()
    }

    override fun onBreak() {
        heatConnections.clear()
        for (i in EnumFacing.values()) {
            val tileOther = world.getTileEntity(pos.offset(i)) ?: continue
            if (tileOther === this) continue
            val handler = NODE_HANDLER!!.fromTile(tileOther, i) ?: continue
            if (handler !is IHeatHandler) continue
            for (j in heatNodes)
                handler.removeConnection(j)
        }
        super.onBreak()
    }
}