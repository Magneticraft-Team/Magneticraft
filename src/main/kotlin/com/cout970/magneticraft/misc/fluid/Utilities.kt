package com.cout970.magneticraft.misc.fluid

import net.minecraft.fluid.Fluid
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import net.minecraftforge.registries.ForgeRegistries

/**
 * Created by cout970 on 2017/08/29.
 */

object VoidFluidHandler : IFluidHandler {

    override fun drain(resource: FluidStack?, doDrain: FluidAction): FluidStack = FluidStack.EMPTY

    override fun drain(maxDrain: Int, doDrain: FluidAction): FluidStack = FluidStack.EMPTY

    override fun fill(resource: FluidStack?, doFill: FluidAction): Int = resource?.amount ?: 0

    override fun getTanks(): Int = 0

    override fun getTankCapacity(tank: Int): Int = 0

    override fun getFluidInTank(tank: Int): FluidStack = FluidStack.EMPTY

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = false
}

fun IFluidHandler.fillFromTank(max: Int, tank: IFluidHandler): Int {
    val canDrain = tank.drain(max, FluidAction.SIMULATE)
    if (canDrain.isNotEmpty) {
        val result = fill(canDrain, FluidAction.SIMULATE)
        if (result > 0) {
            val drained = tank.drain(result, FluidAction.EXECUTE)
            return fill(drained, FluidAction.EXECUTE)
        }
    }
    return 0
}

fun IFluidHandler.isEmpty(): Boolean {
    return (0 until this.tanks).all { getFluidInTank(it).isEmpty }
}

fun IFluidHandler.fillSimulate(resource: FluidStack): Int = fill(resource, FluidAction.SIMULATE)
fun IFluidHandler.fillExecute(resource: FluidStack): Int = fill(resource, FluidAction.EXECUTE)

fun IFluidHandler.drainSimulate(resource: FluidStack): FluidStack = drain(resource, FluidAction.SIMULATE)
fun IFluidHandler.drainExecute(resource: FluidStack): FluidStack = drain(resource, FluidAction.EXECUTE)

fun IFluidHandler.drainSimulate(amount: Int): FluidStack = drain(amount, FluidAction.SIMULATE)
fun IFluidHandler.drainExecute(amount: Int): FluidStack = drain(amount, FluidAction.EXECUTE)

fun FluidStack.orNull(): FluidStack? = if (this.isEmpty) null else this
val FluidStack.isNotEmpty get() = !isEmpty

fun fluid(name: String): Fluid? = ForgeRegistries.FLUIDS.getValue(ResourceLocation(name))

fun Fluid.stack(amount: Int = 1000): FluidStack = FluidStack(this, amount)
fun FluidStack.stack(amount: Int = this.amount): FluidStack = FluidStack(this, amount)