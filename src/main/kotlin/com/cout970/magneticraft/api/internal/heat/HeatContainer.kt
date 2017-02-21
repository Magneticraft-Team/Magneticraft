package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by Yurgen on 19/10/2016.
 */
open class HeatContainer(
        val worldGetter: ()-> World,
        val posGetter: ()-> BlockPos,
        private val emit: Boolean = true,
        private val specificHeat: Double = 1.0,

        //Fraction of temperature difference between current and ambient temperature dissipated per second
        //Evan small values cause rapid heat transfer.  Very large values can cause strange directional transfer behavior
        private var conductivity: Double = DEFAULT_CONDUCTIVITY,

        //Fraction of temperature difference between current and ambient temperature dissipated per second
        //Even small values cause rapid heat dissipation
        private var dissipation: Double = 0.0,

        private val maxHeat: Long = 100,
        private var heat: Long = 0
) : IHeatNode {

    override fun emitsLight(): Boolean = emit
    override fun getDissipation(): Double = dissipation
    override fun getConductivity(): Double = conductivity
    override fun getMaxHeat(): Long = maxHeat
    override fun getHeat(): Long = heat
    override fun getSpecificHeat(): Double = specificHeat

    var ambientTemperature: Double = STANDARD_AMBIENT_TEMPERATURE

    override fun getWorld(): World = worldGetter()

    override fun setDissipation(newDissipation: Double) {
        dissipation = newDissipation
    }

    override fun setConductivity(newConductivity: Double) {
        conductivity = newConductivity
    }

    override fun setHeat(newHeat: Long) {
        heat = newHeat
    }

    override fun getTemperature(): Double {
        return heat / specificHeat
    }

    override fun setAmbientTemp(newAmbient: Double) {
        ambientTemperature = newAmbient
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
        if (dissipation > 0) dissipateHeat()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        heat = nbt.getLong("heat")
        conductivity = nbt.getDouble("conductivity")
        dissipation = nbt.getDouble("dissipation")
        ambientTemperature = nbt.getDouble("ambient")
    }

    override fun serializeNBT() = NBTTagCompound().apply {
        setLong("heat", heat)
        setDouble("conductivity", conductivity)
        setDouble("dissipation", dissipation)
        setDouble("ambient", ambientTemperature)
    }

    override fun getPos(): BlockPos = posGetter()

    fun getHeatFromTemperature(temp: Double): Long {
        return (temp * specificHeat).toLong()
    }

    fun dissipateHeat() {
        val newTemp = ((temperature - ambientTemperature) * (1 - dissipation)) + ambientTemperature
        setHeat(getHeatFromTemperature(newTemp))
    }
}