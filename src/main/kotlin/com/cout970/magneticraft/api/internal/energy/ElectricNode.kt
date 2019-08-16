package com.cout970.magneticraft.api.internal.energy

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.world.isClient
import net.minecraft.nbt.NBTTagCompound


/**
 * Created by cout970 on 11/06/2016.
 */
@Suppress("unused")
open class ElectricNode(
        val ref: ITileRef,
        private val capacity: Double = 1.0,
        private val resistance: Double = 0.001,
        private val name: String = "electric_node_1"
) : IElectricNode {

    private var voltage = 0.0
    private var amperage = 0.0
    var amperageCount = 0.0
    var lastTick = 0L

    override fun getId(): NodeID = NodeID(name, pos, world.provider.dimension)

    override fun getAmperage(): Double {
        updateAmperage()
        return amperage
    }

    fun setAmperage(a: Double) {
        amperage = a
    }

    override fun getVoltage() = voltage

    fun setVoltage(v: Double) {
        voltage = v
    }

    override fun getResistance() = resistance
    override fun getCapacity() = capacity

    override fun getWorld() = ref.world!!
    override fun getPos() = ref.pos!!

    fun updateAmperage() {
        val tick = world.totalWorldTime
        when (tick) {
            lastTick -> return
            lastTick + 1 -> {
                lastTick = tick
                amperage = amperageCount //* 0.5
                amperageCount = 0.0
            }
            else -> {
                if (world.isClient) return
                amperage = 0.0
                amperageCount = 0.0
                lastTick = tick
            }
        }
    }

    override fun applyCurrent(current: Double) {
        updateAmperage()
        amperageCount += Math.abs(current)
        voltage += current / getCapacity()
    }

    override fun applyPower(power: Double, simulated: Boolean): Double {
        val energy = Math.abs(voltage * voltage * getCapacity() + power)
        val finalVoltage = Math.sqrt(Math.max(0.0, energy / getCapacity()))
        val current = getCapacity() * (finalVoltage - voltage)
        if (!simulated) applyCurrent(current)
        return Math.abs(power)
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt == null) return
        voltage = nbt.getDouble("V")
        amperage = nbt.getDouble("A")
    }

    override fun serializeNBT() = newNbt {
        setDouble("V", voltage)
        setDouble("A", amperage)
    }

    override fun toString(): String {
        return "ElectricNode(id=$id, resistance=$resistance, capacity=$capacity, voltage=$voltage, amperage=$amperage"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElectricNode) return false

        if (resistance != other.resistance) return false
        if (capacity != other.capacity) return false
        if (name != other.name) return false
        if (voltage != other.voltage) return false
        if (amperage != other.amperage) return false
        if (lastTick != other.lastTick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resistance.hashCode()
        result = 31 * result + capacity.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + voltage.hashCode()
        result = 31 * result + amperage.hashCode()
        result = 31 * result + lastTick.hashCode()
        return result
    }
}