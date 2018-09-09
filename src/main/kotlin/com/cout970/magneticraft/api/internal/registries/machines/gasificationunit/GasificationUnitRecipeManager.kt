package com.cout970.magneticraft.api.internal.registries.machines.gasificationunit

import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipeManager
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

object GasificationUnitRecipeManager : IGasificationUnitRecipeManager {

    private val recipes = mutableListOf<IGasificationUnitRecipe>()

    override fun findRecipe(input: ItemStack): IGasificationUnitRecipe? {
        if (input.isEmpty) return null
        return recipes.find { it.matches(input) }
    }

    override fun getRecipes(): MutableList<IGasificationUnitRecipe> = recipes.toMutableList()

    override fun registerRecipe(recipe: IGasificationUnitRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        if (recipe.itemOutput.isEmpty && recipe.fluidOutput == null) return false

        recipes += recipe
        return true
    }

    override fun removeRecipe(recipe: IGasificationUnitRecipe): Boolean = recipes.remove(recipe)

    override fun createRecipe(input: ItemStack, itemOutput: ItemStack, fluidOutput: FluidStack?,
                              duration: Float, minTemperature: Float, oreDict: Boolean): IGasificationUnitRecipe {
        require(input.isNotEmpty) { "The recipe input must not be empty" }
        require(itemOutput.isNotEmpty || fluidOutput != null) { "The recipe must have at least one output" }
        return GasificationUnitRecipe(input, itemOutput, fluidOutput, duration, minTemperature, oreDict)
    }
}