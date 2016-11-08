package com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress

import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 22/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getHydraulicPressRecipeManager() instead
 */
object KilnRecipeManager : IKilnRecipeManager {

    private val recipes = mutableListOf<IKilnRecipe>()

    override fun findRecipe(input: ItemStack?): IKilnRecipe? {
        return recipes.filter { it.matches(input) }.firstOrNull()
    }

    override fun getRecipes(): MutableList<IKilnRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: IKilnRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun createRecipe(input: ItemStack, output: ItemStack, ticks: Float, minTemp: Double, maxTemp: Double, oreDict: Boolean): IKilnRecipe {
        return KilnRecipe(input, output, ticks, minTemp, maxTemp, oreDict)
    }
}