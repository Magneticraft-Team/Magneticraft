package com.cout970.magneticraft.misc.fluid

import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.gui.DATA_ID_FLUID_AMOUNT
import com.cout970.magneticraft.systems.gui.DATA_ID_FLUID_NAME
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank

/**
 * Created by cout970 on 10/07/2016.
 */
open class Tank(
    capacity: Int,
    var fluidFilter: (FluidStack) -> Boolean = { true },
    val allowInput: Boolean = true,
    val allowOutput: Boolean = true
) : FluidTank(capacity) {

    var clientFluidAmount = 0
    var clientFluidName = ""

    fun isNonEmpty() = !isEmpty

    override fun isFluidValid(stack: FluidStack): Boolean = fluidFilter(stack)

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if(!allowInput) return 0
        return super.fill(resource, action)
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if(!allowOutput) return FluidStack.EMPTY
        return super.drain(resource, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if(!allowOutput) return FluidStack.EMPTY
        return super.drain(maxDrain, action)
    }

    //server only
    fun getData(): IBD {
        val data = IBD()
        data.setInteger(DATA_ID_FLUID_AMOUNT, getFluid().amount)
        data.setString(DATA_ID_FLUID_NAME, getFluid().fluid.registryName.toString())
        return data
    }

    //client only
    fun setData(ibd: IBD) {
        ibd.getInteger(DATA_ID_FLUID_AMOUNT) { clientFluidAmount = it }
        ibd.getString(DATA_ID_FLUID_NAME) { clientFluidName = it }
    }
}