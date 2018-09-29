package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipe
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.fluid.Tank
import net.minecraftforge.fluids.FluidStack

class OilHeaterCraftingProcess(
    val inputTank: Tank,
    val outputTank: Tank
) : IHeatCraftingProcess {

    private var cacheKey: FluidStack? = null
    private var cacheValue: IOilHeaterRecipe? = null

    private fun getRecipe(input: FluidStack): IOilHeaterRecipe? {
        cacheKey?.let { key ->
            if (key.fluid == input.fluid) return cacheValue
        }

        val recipe = MagneticraftApi.getOilHeaterRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val input = inputTank.fluid ?: return
        val recipe = getRecipe(input) ?: return

        inputTank.drain(recipe.input.amount, true)
        outputTank.fill(recipe.output, true)
    }

    override fun canCraft(): Boolean {
        val input = inputTank.fluid ?: return false

        //check recipe
        val recipe = getRecipe(input) ?: return false

        if (inputTank.fluidAmount < recipe.input.amount) return false

        if ((outputTank.capacity - outputTank.fluidAmount) < recipe.output.amount) return false

        return true
    }

    override fun minTemperature(): Float {
        return inputTank.fluid?.let { getRecipe(it) }?.minTemperature() ?: STANDARD_AMBIENT_TEMPERATURE.toFloat()
    }

    override fun duration(): Float = inputTank.fluid?.let { getRecipe(it) }?.duration ?: 10f
}