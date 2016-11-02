package com.cout970.magneticraft.api.internal.registries.machines.tablesieve

import com.cout970.magneticraft.api.registries.machines.heatexchanger.IIceboxRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 16/06/2016.
 */
data class IceboxRecipe(
        private val input: ItemStack,
        private val output: FluidStack,
        private val heat: Long,
        private val specificHeat: Double,
        private val minTemp: Double,
        private val maxTemp: Double,
        private val reverse: Boolean
) : IIceboxRecipe {

    override fun getInput(): ItemStack = input.copy()

    override fun getOutput(): FluidStack = output.copy()

    override fun getHeat(): Long = heat
    override fun getSpecificHeat(): Double = specificHeat
    override fun getMinTemp(): Double = minTemp
    override fun getMaxTemp(): Double = maxTemp
    override fun getReverse(): Boolean = reverse

    override fun getTotalHeat(temp: Double): Long {
        if (temp < minTemp) return 0
        if (temp > maxTemp) return (specificHeat * (maxTemp - minTemp)).toLong() + heat
        return (specificHeat * (temp - minTemp)).toLong() + heat
    }

    override fun matches(input: ItemStack?): Boolean = input?.isItemEqual(this.input) ?: false
    override fun matchesReverse(output: FluidStack?): Boolean = output?.isFluidEqual(this.output) ?: false
}
