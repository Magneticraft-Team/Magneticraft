package com.cout970.magneticraft.misc.fluid

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

class TankCapabilityFilter(val tank: Tank, val canFill: Boolean, val canDrain: Boolean) : IFluidHandler {

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if (!canDrain) return null
        return tank.drain(resource, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (!canDrain) return null
        return tank.drain(maxDrain, doDrain)
    }

    override fun fill(resource: FluidStack?, doFill: Boolean): Int {
        if (!canFill) return 0
        return tank.fill(resource, doFill)
    }

    override fun getTankProperties(): Array<IFluidTankProperties> = arrayOf(
            FluidTankProperties(tank.fluid, tank.fluidAmount, canFill, canDrain)
    )
}