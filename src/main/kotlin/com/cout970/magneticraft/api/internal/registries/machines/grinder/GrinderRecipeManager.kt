package com.cout970.magneticraft.api.internal.registries.machines.grinder

import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 22/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getGrinderRecipeManager() instead
 */
object GrinderRecipeManager : IGrinderRecipeManager {

    private val recipes = mutableListOf<IGrinderRecipe>()

    override fun findRecipe(input: ItemStack?): IGrinderRecipe? {
        return recipes.filter { it.matches(input) }.firstOrNull()
    }

    override fun getRecipes(): MutableList<IGrinderRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: IGrinderRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun createRecipe(input: ItemStack, output: ItemStack, ticks: Float, oreDict: Boolean): IGrinderRecipe {
        return GrinderRecipe(input, output, ticks, oreDict)
    }
}