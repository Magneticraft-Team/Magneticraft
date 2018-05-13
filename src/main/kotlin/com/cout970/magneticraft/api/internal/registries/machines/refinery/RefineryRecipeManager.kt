package com.cout970.magneticraft.api.internal.registries.machines.refinery

import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipe
import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipeManager
import net.minecraftforge.fluids.FluidStack
import java.util.*

object RefineryRecipeManager : IRefineryRecipeManager {

    private val recipes = mutableListOf<IRefineryRecipe>()

    override fun findRecipe(input: FluidStack?): IRefineryRecipe? {
        return recipes.firstOrNull { it.matches(input) }
    }

    override fun getRecipes(): MutableList<IRefineryRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: IRefineryRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun removeRecipe(recipe: IRefineryRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return recipes.remove(recipe)
        }
        return false
    }

    override fun createRecipe(input: FluidStack, output0: FluidStack?, output1: FluidStack?, output2: FluidStack?, duration: Float): IRefineryRecipe {
        return RefineryRecipe(input, output0, output1, output2, duration)
    }
}