package com.cout970.magneticraft.api.internal.registries.machines.refinery

import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipe
import net.minecraftforge.fluids.FluidStack

class RefineryRecipe(
    private val input: FluidStack,
    private val output0: FluidStack?,
    private val output1: FluidStack?,
    private val output2: FluidStack?,
    private val duration: Float
) : IRefineryRecipe {

    override fun getInput(): FluidStack = input.copy()

    override fun getOutput0(): FluidStack? = output0?.copy()
    override fun getOutput1(): FluidStack? = output1?.copy()
    override fun getOutput2(): FluidStack? = output2?.copy()

    override fun getDuration(): Float = duration

    override fun matches(input: FluidStack?): Boolean {
        if (input == null) return false
        return input.fluid == this.input.fluid
    }
}