package com.cout970.magneticraft.api.internal.registries.machines.sluicebox

import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 16/06/2016.
 */
/**
 * Internal class only, please use MagneticraftApi.getSluiceBoxRecipeManager() instead
 */
object SluiceBoxRecipeManager : ISluiceBoxRecipeManager {

    private val recipes = LinkedList<ISluiceBoxRecipe>()

    override fun findRecipe(stack: ItemStack): ISluiceBoxRecipe? {
        return recipes.firstOrNull { it.matches(stack) }
    }

    override fun registerRecipe(recipe: ISluiceBoxRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return false
        }
        recipes.add(recipe)
        return true
    }

    override fun removeRecipe(recipe: ISluiceBoxRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return recipes.remove(recipe)
        }
        return false
    }

    override fun getRecipes(): List<ISluiceBoxRecipe> = Collections.synchronizedList(recipes)

    override fun createRecipe(input: ItemStack, primaryOutput: ItemStack, secondaryOutput: List<Pair<ItemStack, Float>>,
                              oreDict: Boolean): ISluiceBoxRecipe {
        return SluiceBoxRecipe(input, listOf(primaryOutput to 1f) + secondaryOutput, oreDict)
    }

    override fun createRecipe(input: ItemStack, outputs: MutableList<Pair<ItemStack, Float>>, oreDict: Boolean)
        : ISluiceBoxRecipe {
        return SluiceBoxRecipe(input, outputs, oreDict)
    }
}
