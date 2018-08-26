package com.cout970.magneticraft.misc.fluid

import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

class TankCapabilityFilter(val tank: Tank, val canFill: Boolean = true, val canDrain: Boolean = true) : IFluidHandler {

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

fun wrapWithFluidFilter(func: (FluidStack) -> Boolean): (IFluidHandler, EnumFacing?) -> IFluidHandler {
    return { handler, _ -> FilteredFluidHandler(handler, func) }
}

class FilteredFluidHandler(val source: IFluidHandler, val filter: (FluidStack) -> Boolean) : IFluidHandler {

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if (!filter(resource))
            return null

        return source.drain(resource, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        return source.drain(maxDrain, doDrain)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        if (!filter(resource))
            return 0

        return source.fill(resource, doFill)
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return source.tankProperties
    }
}