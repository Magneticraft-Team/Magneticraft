package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by Yurgen on 19/10/2016.
 */
open class HeatContainer(
        val tile: TileEntity,
        private val specificHeat: Double = 1.0,
        private val conductivity: Double = 0.05, //Fraction of temperature difference between current and ambient temperture dissipated per second
        //Evan small calues cause rapid heat transfer.  Very large values can cause strange directional transfer behavior
        private val dissipation: Double = 0.0, //Fraction of temperature difference between current and ambient temperture dissipated per second
        //Even small values cause rapid heat dissipation
        private val maxHeat: Long = 100,
        private var heat: Long = 0
) : IHeatNode {

    override fun getDissipation(): Double = dissipation
    override fun getConductivity(): Double = conductivity
    override fun getMaxHeat(): Long = maxHeat
    override fun getHeat(): Long = heat
    override fun getSpecificHeat(): Double = specificHeat

    var ambientTemperatureCache: Double = STANDARD_AMBIENT_TEMPERATURE

    override fun getWorld(): World = tile.world

    override fun setHeat(newHeat: Long) {
        heat = newHeat
    }

    override fun getTemperature(): Double {
        return heat / specificHeat
    }

    override fun setAmbientTemp(newAmbient: Double) {
        ambientTemperatureCache = newAmbient
    }

    override fun getMaxTemperature(): Double {
        return maxHeat / specificHeat
    }

    override fun pushHeat(heatIn: Long, simulate: Boolean): Long {
        if (heat + heatIn > maxHeat) {
            val leftover = heat + heatIn - maxHeat
            if (!simulate) heat = maxHeat
            return leftover
        } else {
            if (!simulate) heat += heatIn
            return 0
        }
    }

    override fun pullHeat(heatOut: Long, simulate: Boolean): Long {
        if (heat - heatOut > 0) {
            if (!simulate) heat -= heatOut
            return heatOut
        } else {
            if (!simulate) heat = 0
            return heat
        }
    }

    override fun onOverTemperature() {
        //Default behavior is to do nothing
    }

    override fun updateHeat() {
        if (dissipation > 0) {
            dissipateHeat()
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt == null) return
        heat = nbt.getLong("heat")
        ambientTemperatureCache = nbt.getDouble("ambient")
    }

    override fun serializeNBT() = NBTTagCompound().apply {
        setLong("heat", heat)
        setDouble("ambient", ambientTemperatureCache)
    }
    override fun getPos(): BlockPos {
        return tile.pos
    }

    fun getHeatFromTemperature(Temp: Double): Long {
        return (Temp * specificHeat).toLong()
    }

    fun dissipateHeat() {
        val newTemp = ((temperature - ambientTemperatureCache) * dissipation) + ambientTemperatureCache
        setHeat(getHeatFromTemperature(newTemp))
    }
}