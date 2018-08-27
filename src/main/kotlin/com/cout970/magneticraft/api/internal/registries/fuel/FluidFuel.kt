package com.cout970.magneticraft.api.internal.registries.fuel

import com.cout970.magneticraft.api.registries.fuel.IFluidFuel
import net.minecraftforge.fluids.FluidStack

data class FluidFuel(
    private val fluid: FluidStack,
    private val totalBurningTime: Int,
    private val powerPerCycle: Double
) : IFluidFuel {

    override fun getFluid(): FluidStack = fluid.copy()

    override fun getTotalBurningTime(): Int = totalBurningTime

    override fun getPowerPerCycle(): Double = powerPerCycle

    override fun matches(stack: FluidStack): Boolean = stack.isFluidEqual(fluid)
}