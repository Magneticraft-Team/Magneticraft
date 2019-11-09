package com.cout970.magneticraft.misc.fluid

import com.cout970.magneticraft.EnumFacing
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.IFluidHandler

class TankCapabilityFilter(val tank: Tank, val canFill: Boolean = true, val canDrain: Boolean = true) : IFluidHandler {
    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if (!canDrain) return FluidStack.EMPTY
        return tank.drain(resource, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if (!canDrain) return FluidStack.EMPTY
        return tank.drain(maxDrain, action)
    }

    override fun getTankCapacity(index: Int): Int = tank.capacity

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (!canFill) return 0
        return tank.fill(resource, action)
    }

    override fun getFluidInTank(index: Int): FluidStack = tank.fluid

    override fun getTanks(): Int = 1

    override fun isFluidValid(index: Int, stack: FluidStack): Boolean = tank.isFluidValid(stack)
}

fun wrapWithFluidFilter(func: (FluidStack) -> Boolean): (IFluidHandler, EnumFacing?) -> IFluidHandler {
    return { handler, _ -> FilteredFluidHandler(handler, func) }
}

class FilteredFluidHandler(val source: IFluidHandler, val filter: (FluidStack) -> Boolean) : IFluidHandler {
    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction?): FluidStack {
        if (!filter(resource)) return FluidStack.EMPTY
        return source.drain(resource, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction?): FluidStack {
        return source.drain(maxDrain, action)
    }

    override fun getTankCapacity(index: Int): Int = source.getTankCapacity(index)

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (!filter(resource)) return 0
        return source.fill(resource, action)
    }

    override fun getFluidInTank(index: Int): FluidStack = source.getFluidInTank(index)

    override fun getTanks(): Int = 1

    override fun isFluidValid(index: Int, stack: FluidStack): Boolean = source.isFluidValid(index, stack)
}

class FluidHandlerConcatenate(val tanks: List<IFluidTank>) : IFluidHandler {
    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return tanks.asSequence()
            .map { it.drain(resource, action) }
            .find { it.isNotEmpty } ?: FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return tanks.asSequence()
            .map { it.drain(maxDrain, action) }
            .find { it.isNotEmpty } ?: FluidStack.EMPTY
    }

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        val stack = resource.copy()
        var sum = 0
        tanks.forEach { tank ->
            val accepted = tank.fill(stack, action)
            sum += accepted
            stack.shrink(accepted)
            if (stack.isEmpty) return@forEach
        }
        return sum
    }

    override fun getTankCapacity(tank: Int): Int = tanks[tank].capacity

    override fun getFluidInTank(tank: Int): FluidStack = tanks[tank].fluid

    override fun getTanks(): Int = tanks.size

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = tanks[tank].isFluidValid(stack)
}