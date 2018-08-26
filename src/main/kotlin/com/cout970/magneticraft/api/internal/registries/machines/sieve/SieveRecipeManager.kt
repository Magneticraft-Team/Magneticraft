package com.cout970.magneticraft.api.internal.registries.machines.sieve

import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 22/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getSifterRecipeManager() instead
 */
object SieveRecipeManager : ISieveRecipeManager {

    private val recipes = mutableListOf<ISieveRecipe>()

    override fun findRecipe(input: ItemStack): ISieveRecipe? {
        return recipes.firstOrNull { it.matches(input) }
    }

    override fun getRecipes(): MutableList<ISieveRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: ISieveRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun removeRecipe(recipe: ISieveRecipe?): Boolean = recipes.remove(recipe)

    override fun createRecipe(input: ItemStack, primary: ItemStack, primaryChance: Float, secondary: ItemStack,
                              secondaryChance: Float, tertiary: ItemStack, tertiaryChance: Float, duration: Float,
                              oreDict: Boolean): ISieveRecipe {

        return SieveRecipe(input, primary, primaryChance, secondary, secondaryChance, tertiary, tertiaryChance,
            duration, oreDict)
    }
}