package com.cout970.magneticraft.api.internal.registries.machines.kiln

import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipeManager
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by Yurgen on 22/08/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getKilnRecipeManager() instead
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

    override fun createRecipe(input: ItemStack, output: ItemStack, duration: Int, minTemp: Double, maxTemp: Double, oreDict: Boolean): IKilnRecipe {
        return KilnRecipe(input, output, null, duration, minTemp, maxTemp, oreDict)
    }

    override fun createRecipe(input: ItemStack, output: IBlockState, duration: Int, minTemp: Double, maxTemp: Double, oreDict: Boolean): IKilnRecipe {
        return KilnRecipe(input, null, output, duration, minTemp, maxTemp, oreDict)
    }
}