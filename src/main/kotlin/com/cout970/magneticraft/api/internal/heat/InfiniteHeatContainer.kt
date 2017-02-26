package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 * Created by Yurgen on 19/10/2016.
 */
class InfiniteHeatContainer(
        tile: TileEntity,
        conductivity: Double = DEFAULT_CONDUCTIVITY,
        private val temperature: Double
) : HeatContainer({ tile.world }, { tile.pos }, conductivity = conductivity, specificHeat = 0.0, dissipation = 0.0) {

    override fun getTemperature(): Double {
        return temperature
    }

    override fun getMaxTemperature(): Double {
        return temperature
    }

    override fun applyHeat(heatArg: Double, simulate: Boolean): Double {
        return Math.max(0.0, -heatArg)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) = Unit

    override fun serializeNBT() = newNbt {  }

    override fun iterate() = Unit
}