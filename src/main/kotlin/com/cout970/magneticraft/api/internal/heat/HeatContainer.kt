package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.guessAmbientTemp
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by Yurgen on 19/10/2016.
 */
open class HeatContainer(
        private val worldGetter: () -> World,
        private val posGetter: () -> BlockPos,
        private var specificHeat: Double = 1.0,
        private var conductivity: Double = DEFAULT_CONDUCTIVITY,
        private var dissipation: Double = 0.0,
        private val maxHeat: Double = 100.0,
        private var heat: Double = 0.0
) : IHeatNode {

    override fun getHeat(): Double = heat
    override fun getMaxHeat(): Double = maxHeat
    override fun getSpecificHeat(): Double = specificHeat
    override fun getConductivity(): Double = conductivity
    override fun getDissipation(): Double = dissipation

    fun setSpecificHeat(value: Double) { specificHeat = value }
    fun setConductivity(value: Double) { conductivity = value }
    fun setDissipation(value: Double) { dissipation = value }

    override fun getWorld(): World = worldGetter()
    override fun getPos(): BlockPos = posGetter()

    fun setHeat(newHeat: Double) {
        heat = newHeat
    }

    override fun getTemperature(): Double {
        return heat / specificHeat
    }

    override fun getMaxTemperature(): Double {
        return maxHeat / specificHeat
    }

    override fun applyHeat(heatArg: Double, simulate: Boolean): Double {

        if (heatArg > 0) { // insert heat

            if (heat + heatArg > maxHeat) {
                // more heat than space
                val accepted = maxHeat - heat
                if (!simulate) heat = maxHeat
                return accepted
            } else {
                // less heat than space
                if (!simulate) heat += heatArg
                return heatArg
            }
        } else { // extract heat

            if (heat - heatArg > 0) {
                // less heat than storage
                if (!simulate) heat -= heatArg
                return heatArg
            } else {
                //more heat than storage
                if (!simulate) heat = 0.0
                return heat
            }
        }
    }

    override fun iterate() {
        if (dissipation > 0) {
            dissipateHeat()
        }
    }

    fun dissipateHeat() {
        val ambientTemperature = guessAmbientTemp(world, pos)
        val newTemp = ((temperature - ambientTemperature) * (1 - dissipation)) + ambientTemperature
        setHeat(newTemp * specificHeat)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        heat = nbt.getDouble("heat")
    }

    override fun serializeNBT() = newNbt { add("heat", heat) }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        var result = specificHeat.hashCode()
        result = 31 * result + conductivity.hashCode()
        result = 31 * result + dissipation.hashCode()
        result = 31 * result + maxHeat.hashCode()
        result = 31 * result + heat.hashCode()
        return result
    }
}