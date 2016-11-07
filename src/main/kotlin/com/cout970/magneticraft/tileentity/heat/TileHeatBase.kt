package com.cout970.magneticraft.tileentity.electric

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

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileHeatBase : TileBase(), ITickable, IHeatHandler {

    abstract val heatNodes: List<IHeatNode>
    val heatConnections = mutableListOf<IHeatConnection>()
    val lightLevelUpdateDelay = 20
    var lightLevelCache = 0.0f
    var initiated = false
    var firstTicks = -1

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
        if (initiated == false) {
            updateHeatConnections()
            for (i in heatNodes) i.heat = (biomeTemptoKelvin(world, pos) * i.specificHeat).toLong()
            initiated = true
        }
    }

    override fun updateHeatConnections() {
        for (i in heatNodes) {
            heatConnections.clear() //Don't do this for internal heat connections
            for (j in EnumFacing.values()) {
                val tileOther = world.getTileEntity(pos.offset(j)) ?: continue
                if (tileOther === this) continue
                val handler = NODE_HANDLER!!.fromTile(tileOther) ?: continue
                if (handler !is IHeatHandler) continue
                for (otherNode in handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }) {
                    heatConnections.add(HeatConnection(i, otherNode))
                }
            }
            i.setAmbientTemp(biomeTemptoKelvin(world, pos)) //This might be unnecessary
        }
    }

    override fun addConnection(connection: IHeatConnection) {
        heatConnections.add(connection)
    }

    override fun getNodes(): List<INode> = heatNodes


    override fun removeConnection(connection: IHeatConnection) {
        heatConnections.remove(connection)
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
        setBoolean("initiated", initiated)
    }

    override fun load(nbt: NBTTagCompound) {
        lightLevelCache = nbt.getFloat("lightLevelCache")
        initiated = nbt.getBoolean("initiated")
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