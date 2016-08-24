package com.cout970.magneticraft.api.internal.registries.machines.crushingtable

import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 24/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getCrushingTableRecipeManager() instead
 */
object CrushingTableRecipeManager : ICrushingTableRecipeManager {

    private val recipes = mutableListOf<ICrushingTableRecipe>()

    override fun findRecipe(input: ItemStack?): ICrushingTableRecipe? {
        return recipes.filter { it.matches(input) }.firstOrNull()
    }

    override fun getRecipes(): MutableList<ICrushingTableRecipe> = Collections.synchronizedList(recipes)

    override fun registerRecipe(recipe: ICrushingTableRecipe): Boolean {
        if (findRecipe(recipe.input) != null) return false
        recipes.add(recipe)
        return true
    }

    override fun createRecipe(input: ItemStack, output: ItemStack, oreDict: Boolean): ICrushingTableRecipe {
        return CrushingTableRecipe(input, output, oreDict)
    }
}