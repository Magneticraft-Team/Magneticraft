package com.cout970.magneticraft.systems.integration.buildcraft

import buildcraft.api.fuels.BuildcraftFuelRegistry
import buildcraft.api.fuels.IFuel
import buildcraft.api.fuels.IFuelManager
import buildcraft.api.mj.MjAPI
import com.cout970.magneticraft.api.internal.registries.fuel.FluidFuel
import com.cout970.magneticraft.api.registries.fuel.IFluidFuel
import com.cout970.magneticraft.api.registries.fuel.IFluidFuelManager
import com.cout970.magneticraft.misc.ConversionTable
import net.minecraftforge.fluids.FluidStack

class BuildcraftFuelManager : IFluidFuelManager {

    val parent: IFuelManager get() = BuildcraftFuelRegistry.fuel!!

    override fun findFuel(fluidStack: FluidStack): IFluidFuel? {
        return parent.getFuel(fluidStack)?.wrap()
    }

    override fun getFuels(): List<IFluidFuel> {
        return parent.fuels.map { it.wrap() }
    }

    override fun registerFuel(recipe: IFluidFuel): Boolean {
        return parent.addFuel(recipe.fluid, recipe.powerPerCycle.convertPower(), recipe.totalBurningTime) != null
    }

    override fun removeFuel(recipe: IFluidFuel): Boolean {
        // Unable to remove fuels
        return false
    }

    override fun createFuel(fluidStack: FluidStack, burningTime: Int, powerPerCycle: Double): IFluidFuel {
        return FluidFuel(fluidStack, burningTime, powerPerCycle)
    }

    private fun IFuel.wrap(): IFluidFuel = FluidFuel(fluid, totalBurningTime, powerPerCycle.convertPower())

    private fun Long.convertPower(): Double = ConversionTable.MJ_TO_FE * (this.toDouble() / MjAPI.MJ.toDouble())
    private fun Double.convertPower(): Long = (this * MjAPI.MJ / ConversionTable.MJ_TO_FE).toLong()
}