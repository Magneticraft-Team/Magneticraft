package com.cout970.magneticraft.api.internal.registries.machines.sifter

import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.SifterRecipe
import com.cout970.magneticraft.api.registries.machines.sifter.ISifterRecipe
import com.cout970.magneticraft.api.registries.machines.sifter.ISifterRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 22/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getSifterRecipeManager() instead
 */
object SifterRecipeManager : ISifterRecipeManager {

    private val recipes = mutableListOf<ISifterRecipe>()

    override fun findRecipe(input: ItemStack?): ISifterRecipe? {
        return recipes.filter { it.matches(input) }.firstOrNull()
    }

    override fun getRecipes(): MutableList<ISifterRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: ISifterRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun createRecipe(input: ItemStack, primary: ItemStack, secondary: ItemStack, secondaryChance: Float, tertiary: ItemStack, tertiaryChance: Float, duration: Float, oreDict: Boolean): ISifterRecipe {
        return SifterRecipe(input, primary, secondary, secondaryChance, tertiary, tertiaryChance, duration, oreDict)
    }
}