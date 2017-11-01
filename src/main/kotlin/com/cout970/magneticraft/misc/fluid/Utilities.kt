package com.cout970.magneticraft.misc.fluid

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

/**
 * Created by cout970 on 2017/08/29.
 */

object VoidFluidHandler : IFluidHandler {

    override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? = null

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? = null

    override fun fill(resource: FluidStack?, doFill: Boolean): Int = resource?.amount ?: 0

    override fun getTankProperties(): Array<IFluidTankProperties> = arrayOf(VoidFluidTankProperties)
}

object VoidFluidTankProperties : IFluidTankProperties {

    override fun canDrainFluidType(fluidStack: FluidStack?): Boolean = false

    override fun getContents(): FluidStack? = null

    override fun canFillFluidType(fluidStack: FluidStack?): Boolean = true

    override fun getCapacity(): Int = 16000

    override fun canFill(): Boolean = true

    override fun canDrain(): Boolean = false
}

fun IFluidHandler.fillFromTank(max: Int, tank: IFluidHandler): Int {
    val canDrain = tank.drain(max, false)
    if (canDrain != null) {
        val result = fill(canDrain, false)
        if (result > 0) {
            val drained = tank.drain(result, true)
            return fill(drained, true)
        }
    }
    return 0
}

fun IFluidHandler.isEmpty(): Boolean {
    return tankProperties.none { it.contents != null }
}