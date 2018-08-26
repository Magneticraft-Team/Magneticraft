package com.cout970.magneticraft.api.internal.registries.machines.heatrecipes

import com.cout970.magneticraft.api.registries.machines.heatrecipes.IHeatExchangerRecipe
import net.minecraftforge.fluids.FluidStack

/**
 * Created by Yurgen on 16/06/2016.
 */
data class HeatExchangerRecipe(
    private val input: FluidStack,
    private val output: FluidStack,
    private val heat: Long,
    private val minTemp: Double,
    private val maxTemp: Double,
    private val reverseLow: Boolean,
    private val reverseHigh: Boolean
) : IHeatExchangerRecipe {

    override fun getInput(): FluidStack = input.copy()

    override fun getOutput(): FluidStack = output.copy()

    override fun getHeat(): Long = heat
    override fun getMinTemp(): Double = minTemp
    override fun getMaxTemp(): Double = maxTemp
    override fun getReverseHigh(): Boolean = reverseHigh
    override fun getReverseLow(): Boolean = reverseLow

    override fun matches(input: FluidStack?): Boolean = input?.isFluidEqual(this.input) ?: false
    override fun matchesReverse(output: FluidStack?): Boolean = output?.isFluidEqual(this.output) ?: false
}
