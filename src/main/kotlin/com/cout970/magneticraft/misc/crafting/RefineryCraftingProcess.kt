package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipe
import com.cout970.magneticraft.misc.fluid.Tank
import net.minecraftforge.fluids.FluidStack

class RefineryCraftingProcess(
    val inputTank: Tank,
    val outputTank0: Tank,
    val outputTank1: Tank,
    val outputTank2: Tank
) : ICraftingProcess {

    private var cacheKey: FluidStack? = null
    private var cacheValue: IRefineryRecipe? = null

    private fun getRecipe(input: FluidStack): IRefineryRecipe? {
        cacheKey?.let { key ->
            if (key.fluid == input.fluid) return cacheValue
        }

        val recipe = MagneticraftApi.getRefineryRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val input = inputTank.fluid ?: return
        val recipe = getRecipe(input) ?: return

        inputTank.drain(recipe.input.amount, true)
        recipe.output0?.let { outputTank0.fill(it, true) }
        recipe.output1?.let { outputTank1.fill(it, true) }
        recipe.output2?.let { outputTank2.fill(it, true) }
    }

    override fun canCraft(): Boolean {
        val input = inputTank.fluid ?: return false

        //check recipe
        val recipe = getRecipe(input) ?: return false

        if (inputTank.fluidAmount < recipe.input.amount) return false

        recipe.output0?.let { out ->
            if ((outputTank0.capacity - outputTank0.fluidAmount) < out.amount) return false
        }
        recipe.output1?.let { out ->
            if ((outputTank1.capacity - outputTank1.fluidAmount) < out.amount) return false
        }
        recipe.output2?.let { out ->
            if ((outputTank2.capacity - outputTank2.fluidAmount) < out.amount) return false
        }

        return true
    }

    override fun duration(): Float = inputTank.fluid?.let { getRecipe(it) }?.duration ?: 10f
}