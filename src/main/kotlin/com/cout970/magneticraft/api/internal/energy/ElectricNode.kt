package com.cout970.magneticraft.api.internal.energy

import com.cout970.magneticraft.api.energy.IElectricNode
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 11/06/2016.
 */
@Suppress("unused")
open class ElectricNode(
        private val worldGetter: () -> World,
        private val posGetter: () -> BlockPos,
        private val resistance: Double = 0.001,
        private val capacity: Double = 1.0
) : IElectricNode {

    private var voltage = 0.0
    private var amperage = 0.0
    var amperageCount = 0.0
    var lastTick = 0L

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

    override fun getWorld() = worldGetter()
    override fun getPos() = posGetter()

    fun updateAmperage() {
        val tick = world.totalWorldTime
        if (tick == lastTick) {
            return
        } else if (tick == lastTick + 1) {
            lastTick = tick
            amperage = amperageCount * 0.5
            amperageCount = 0.0
        } else {
            amperage = 0.0
            amperageCount = 0.0
            lastTick = tick
        }
    }

    override fun applyCurrent(current: Double) {
        updateAmperage()
        amperageCount += Math.abs(current)
        voltage += current / getCapacity()
    }

    override fun applyPower(power: Double, simulated: Boolean): Double {
        if (power > 0) {
            val squared = voltage * voltage + Math.abs(power) * 2
            val diff = Math.sqrt(squared) - Math.abs(voltage)
            if (!simulated) applyCurrent(diff)
            return power
        } else {
            val squared = voltage * voltage - Math.abs(power) * 2
            val powerUsed = if (squared > 0) -power else (voltage * voltage) / 2
            val diff = Math.sqrt(Math.max(squared, 0.0)) - Math.abs(voltage)
            if (!simulated) applyCurrent(diff)
            return powerUsed
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt == null) return
        voltage = nbt.getDouble("V")
        amperage = nbt.getDouble("A")
    }

    override fun serializeNBT() = NBTTagCompound().apply {
        setDouble("V", voltage)
        setDouble("A", amperage)
    }

    override fun toString(): String {
        return "ElectricNode(world=${worldGetter.invoke()}, pos=${posGetter.invoke()}, resistance=$resistance, capacity=$capacity, voltage=$voltage, amperage=$amperage"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElectricNode) return false

        if (worldGetter.invoke() != other.worldGetter.invoke()) return false
        if (posGetter.invoke() != other.posGetter.invoke()) return false
        if (resistance != other.resistance) return false
        if (capacity != other.capacity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = worldGetter.invoke().hashCode()
        result = 31 * result + posGetter.invoke().hashCode()
        result = 31 * result + resistance.hashCode()
        result = 31 * result + capacity.hashCode()
        return result
    }
}