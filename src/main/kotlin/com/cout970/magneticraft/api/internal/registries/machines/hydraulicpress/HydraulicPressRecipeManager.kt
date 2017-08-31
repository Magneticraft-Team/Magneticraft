package com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress

import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 22/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getHydraulicPressRecipeManager() instead
 */
object HydraulicPressRecipeManager : IHydraulicPressRecipeManager {

    private val recipes = mutableListOf<IHydraulicPressRecipe>()

    override fun findRecipe(input: ItemStack): IHydraulicPressRecipe? {
        return recipes.firstOrNull { it.matches(input) }
    }

    override fun getRecipes(): MutableList<IHydraulicPressRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: IHydraulicPressRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun createRecipe(input: ItemStack, output: ItemStack, ticks: Float, oreDict: Boolean): IHydraulicPressRecipe {
        return HydraulicPressRecipe(input, output, ticks, oreDict)
    }
}