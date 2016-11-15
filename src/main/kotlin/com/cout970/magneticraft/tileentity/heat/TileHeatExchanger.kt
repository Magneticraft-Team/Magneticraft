package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.cout970.magneticraft.util.fluid.Tank
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 04/07/2016.
 */
class TileHeatExchanger() : TileHeatBase() {

    val itank: Tank = object : Tank(4000) {
        override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
    }

    val otank: Tank = object : Tank(4000) {
        override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == FLUID_HANDLER) {
            if (facing == EnumFacing.DOWN) return itank as T
            else return otank as T
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == FLUID_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT).toLong(),
            conductivity = DEFAULT_CONDUCTIVITY,
            tile = this)

    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)
}