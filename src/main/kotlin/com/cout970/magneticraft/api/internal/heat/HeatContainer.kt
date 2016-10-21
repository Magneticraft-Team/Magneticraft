package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.heat.IHeatContainer
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.toKelvinFromCelsius
import com.cout970.magneticraft.util.toKelvinFromMinecraftUnits
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * Created by Yurgen on 19/10/2016.
 */
open class HeatContainer(
        val tile: TileEntity,
        private val specificHeat: Double = 1,
        private val conductivity: Double = 0.05, //Fraction of temperature difference between current and ambient temperture dissipated per second
        //Evan small calues cause rapid heat transfer.  Very large values can cause strange directional transfer behavior
        private val dissipation: Double = 0, //Fraction of temperature difference between current and ambient temperture dissipated per second
        //Even small values cause rapid heat dissipation
        private val maxHeat: Long = 100,
        private var heat: Long = (STANDARD_AMBIENT_TEMPERATURE.toKelvinFromCelsius() * this.specificHeat).toLong()
) : IHeatContainer {

    var ambientTemperatureCache: Double = STANDARD_AMBIENT_TEMPERATURE

    override fun setHeat(newHeat: Long) {
        heat = newHeat
    }

    override fun getTemperature(): Double {
        return heat / specificHeat
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

    val transferDelayMax: Int = 20 //1 per second
    var transferDelay: Int = transferDelayMax
    var activeConnections: MutableList<IHeatContainer> = ArrayList()

    override fun onOverTemperature() {
        //Default behavior is to do nothing
    }

    override fun refreshConnections() {
        activeConnections.clear()
        for (i in EnumFacing.values()) {
            val temp = tile.world.getTileEntity(tile.pos.offset(i)) ?: continue
        }
        ambientTemperatureCache = tile.world.getBiome(tile.pos).temperature.toKelvinFromMinecraftUnits()
    }

    override fun getConnections(): List<IHeatContainer> {
        return activeConnections
    }

    override fun updateHeat() {
        if (transferDelay == 0) {
            if (dissipation > 0) {
                dissipateHeat()
            }
            transferDelay = transferDelayMax
            if (conductivity > 0) {
                val connectionList = connections
                for (i in connectionList) {
                    if (temperature > i.temperature) {
                        val minConductivity = Math.min(conductivity, i.conductivity) //Use the lowest conductivity.
                        var heatToTransfer = (Math.floor((temperature - i.temperature) * minConductivity)).toLong()
                        heatToTransfer -= i.pushHeat(heatToTransfer, false) //If the block accepts all the heat, we subtract all of it
                        heat -= heatToTransfer                              //If there's any leftover, we effectively add it back
                    }
                }
            }
        } else {
            transferDelay--
        }
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