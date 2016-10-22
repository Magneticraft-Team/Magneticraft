package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.HeatConnection
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.util.shouldTick
import com.cout970.magneticraft.util.toKelvinFromMinecraftUnits
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileElectricHeatBase : TileElectricBase(), IHeatHandler {

    abstract val heatNodes: List<IHeatNode>
    val heatConnections = mutableListOf<IHeatConnection>()

    override fun update() {
        if (shouldTick(20)) {
            if (!worldObj.isRemote) {
                heatNodes.forEach { it.updateHeat() }
                heatConnections.forEach { it.iterate() }
            }
        }
        super.update()
    }

    override fun onLoad() {
        super.onLoad()
        for (i in heatNodes) i.heat = (world.getBiome(pos).temperature.toKelvinFromMinecraftUnits() * i.specificHeat).toLong()
    }

    override fun updateHeatConnections() {
        for (i in heatNodes) {
            heatConnections.clear() //Don't do this for internal heat connections
            for (j in EnumFacing.values()) {
                val tileOther = world.getTileEntity(pos.offset(j)) ?: continue
                val handler = NODE_HANDLER!!.fromTile(tileOther) ?: continue
                if (handler !is IHeatHandler) continue
                for (otherNode in handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }) {
                    heatConnections.add(HeatConnection(i, otherNode))
                }
            }
            i.setAmbientTemp(world.getBiome(pos).temperature.toKelvinFromMinecraftUnits()) //This might be unnecessary
        }
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

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        val tag = NBTTagCompound()
        for (i in 0 until heatNodes.size) {
            tag.setTag("HeatNode" + i, heatNodes[i].serializeNBT())
        }
        compound!!.setTag("HeatNodes", tag)
        return super.writeToNBT(compound)
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) {
    }

    override fun addConnection(connection: IHeatConnection) {
        heatConnections.add(connection)
    }

    override fun getNodes(): List<INode> = heatNodes + electricNodes

    override fun removeConnection(connection: IHeatConnection) {
        heatConnections.remove(connection)
    }

    override fun getConnections(): List<IHeatConnection> {
        return heatConnections.toList()
    }
}