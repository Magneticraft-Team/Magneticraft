package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.tileentity.TileEntity

/**
 * Created by Yurgen on 19/10/2016.
 */
class InfiniteHeatContainer(
        val tile: TileEntity,
        private val conductivity: Double = 0.05, //Fraction of temperature difference between current and ambient temperture dissipated per second
        //Evan small calues cause rapid heat transfer.
        //Values above 0.5f are guaranteed to be unphysical.  Values above 0.1f are probably unphysical
        private val temperature: Double = 1800.toKelvinFromCelsius()
) : HeatContainer(tile = tile, conductivity = conductivity, specificHeat = 0.0, dissipation = 0.0) {

    override fun getTemperature(): Double {
        return temperature
    }

    override fun getMaxTemperature(): Double {
        return temperature
    }

    override fun pushHeat(heatIn: Long, simulate: Boolean): Long {
        return 0
    }

    override fun pullHeat(heatOut: Long, simulate: Boolean): Long {
        return heatOut
    }

    override fun updateHeat() {
        if (transferDelay == 0) {
            transferDelay = transferDelayMax
            if (conductivity > 0) {
                val connectionList = connections
                for (i in connectionList) {
                    if (temperature > i.temperature) {
                        val minConductivity = Math.min(conductivity, i.conductivity) //Use the lowest conductivity.
                        val heatToTransfer = (Math.floor((temperature - i.temperature) * minConductivity)).toLong()
                        i.pushHeat(heatToTransfer, false)
                    }
                }
            }
        } else {
            transferDelay--
        }
    }
}