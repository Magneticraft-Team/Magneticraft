package com.cout970.magneticraft.api.internal.registries.machines.oilheater

import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipe
import net.minecraftforge.fluids.FluidStack

class OilHeaterRecipe(
    private val input: FluidStack,
    private val output: FluidStack,
    private val duration: Float,
    private val minTemperature: Float
) : IOilHeaterRecipe {

    override fun getInput(): FluidStack = input.copy()

    override fun getOutput(): FluidStack = output.copy()

    override fun getDuration(): Float = duration

    override fun minTemperature(): Float = minTemperature

    override fun matches(input: FluidStack?): Boolean {
        if (input == null) return false
        return input.fluid == this.input.fluid
    }
}