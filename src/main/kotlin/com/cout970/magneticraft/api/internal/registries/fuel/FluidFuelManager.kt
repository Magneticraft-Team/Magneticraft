package com.cout970.magneticraft.api.internal.registries.fuel

import com.cout970.magneticraft.api.registries.fuel.IFluidFuel
import com.cout970.magneticraft.api.registries.fuel.IFluidFuelManager
import net.minecraftforge.fluids.FluidStack
import java.util.*

object FluidFuelManager : IFluidFuelManager {

    /**
     * This field determines if the mod should use it's own FluidFuelManager or use a wrapper around
     * another fluid fuel provider like BuildcraftFuelRegistry.fuel
     *
     * This field is changed in preInit if buildcraft api is found, so all registrations must be done at init
     */
    @JvmField
    internal var FLUID_FUEL_MANAGER: IFluidFuelManager = FluidFuelManager

    private val fuels = mutableListOf<IFluidFuel>()

    override fun findFuel(fluidStack: FluidStack): IFluidFuel? = fuels.firstOrNull { it.matches(fluidStack) }

    override fun getFuels(): MutableList<IFluidFuel> = Collections.unmodifiableList(fuels)

    override fun registerFuel(recipe: IFluidFuel): Boolean {
        if (findFuel(recipe.fluid) != null) return false
        if (recipe.totalBurningTime <= 0.0) return false
        if (recipe.powerPerCycle <= 0.0) return false

        fuels += recipe
        return true
    }

    override fun removeFuel(recipe: IFluidFuel): Boolean = fuels.remove(recipe)

    override fun createFuel(fluidStack: FluidStack, burningTime: Int, powerPerCycle: Double): IFluidFuel {
        return FluidFuel(fluidStack, burningTime, powerPerCycle)
    }
}