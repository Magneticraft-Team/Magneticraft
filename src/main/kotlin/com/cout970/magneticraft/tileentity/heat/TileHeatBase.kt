package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.HeatConnection
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import java.lang.Math.floor

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileHeatBase : TileBase(), ITickable, IHeatHandler {

    abstract val heatNodes: List<IHeatNode>
    val heatConnections: MutableSet<IHeatConnection> = mutableSetOf()
    val lightLevelUpdateDelay = 20
    var lightLevelCache = 0.0f
    var initiated = false
    var firstTicks = -1

    override fun getComparatorOutput(): Int {
        val node = heatNodes.first()
        return floor(node.temperature / node.maxTemperature).toInt()
    }

    override fun update() {
        if (worldObj.isServer) {
            heatNodes.forEach { it.updateHeat() }
            heatConnections.forEach { it.iterate() }
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
                sendUpdateToNearPlayers()
            }
            // when the world is loaded the player take some ticks to
            // load so this wait for the player to send an update
            if (firstTicks > 0) {
                firstTicks--
                if (firstTicks % 20 == 0) {
                    sendUpdateToNearPlayers()
                }
            }
        }
    }

    override fun onLoad() {
        super.onLoad()
        if (!initiated) {
            updateHeatConnections()
            for (i in heatNodes) i.heat = (guessAmbientTemp(world, pos) * i.specificHeat).toLong()
            initiated = true
        }
    }

    override fun updateHeatConnections() {
        for (i in heatNodes) {
            for (j in EnumFacing.values()) {
                val tileOther = world.getTileEntity(pos.offset(j)) ?: continue
                if (tileOther === this) continue
                val handler = (NODE_HANDLER!!.fromTile(tileOther, j) ?: continue) as? IHeatHandler ?: continue
                for (otherNode in handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }) {
                    heatConnections.add(HeatConnection(i, otherNode))
                    handler.addConnection(HeatConnection(otherNode, i))
                }
            }
            i.setAmbientTemp(guessAmbientTemp(world, pos)) //This might be unnecessary
        }
    }

    override fun addConnection(connection: IHeatConnection) {
        heatConnections.add(connection)
    }

    override fun getNodes(): List<INode> = heatNodes


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

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("HeatNodes")) {
            val tag = compound.getCompoundTag("HeatNodes")
            for (i in 0 until heatNodes.size) {
                heatNodes[i].deserializeNBT(tag.getCompoundTag("HeatNode" + i))
            }
        }
        super.readFromNBT(compound)
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setFloat("lightLevelCache", lightLevelCache)
    }

    override fun load(nbt: NBTTagCompound) {
        lightLevelCache = nbt.getFloat("lightLevelCache")
        initiated = true //Block should be initiated on load, so always load true here
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        val tag = NBTTagCompound()
        for (i in 0 until heatNodes.size) {
            tag.setTag("HeatNode" + i, heatNodes[i].serializeNBT())
        }
        compound!!.setTag("HeatNodes", tag)
        return super.writeToNBT(compound)
    }

    fun interpolate(v: Double, min: Double, max: Double): Double {
        if (v < min) return 0.0
        if (v > max) return 1.0
        return (v - min) / (max - min)
    }

    override fun onBreak() {
        heatConnections.clear()
//       for(i in EnumFacing.values()) {
//           val tileOther = world.getTileEntity(pos.offset(i)) ?: continue
//           if (tileOther === this) continue
//          val handler = NODE_HANDLER!!.fromTile(tileOther, i) ?: continue
//           if (handler !is IHeatHandler) continue
//           for(j in heatNodes)
//               handler.removeConnection(j)
//       }
        super.onBreak()
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == NODE_HANDLER) return this as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == NODE_HANDLER) return true
        return super.hasCapability(capability, facing)
    }
}