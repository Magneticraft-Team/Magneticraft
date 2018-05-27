package com.cout970.magneticraft.api.internal.registries.machines.oilheater

import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipe
import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipeManager
import net.minecraftforge.fluids.FluidStack
import java.util.*

object OilHeaterRecipeManager : IOilHeaterRecipeManager {

    private val recipes = mutableListOf<IOilHeaterRecipe>()

    override fun findRecipe(input: FluidStack): IOilHeaterRecipe? {
        return recipes.firstOrNull { it.matches(input) }
    }

    override fun getRecipes(): MutableList<IOilHeaterRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: IOilHeaterRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun removeRecipe(recipe: IOilHeaterRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return recipes.remove(recipe)
        }
        return false
    }

    override fun createRecipe(input: FluidStack, output: FluidStack, duration: Float, minTemperature: Float): IOilHeaterRecipe {
        return OilHeaterRecipe(input, output, duration, minTemperature)
    }
}