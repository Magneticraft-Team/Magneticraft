package com.cout970.magneticraft.api.internal.registries.generators.thermopile

import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe
import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipeManager
import net.minecraft.block.Block

/**
 * Created by cout970 on 2017/08/28.
 */
object ThermopileRecipeManager : IThermopileRecipeManager {

    private val recipeList = mutableListOf<IThermopileRecipe>()

    override fun findRecipe(input: Block?): IThermopileRecipe? {
        if(input == null) return null
        return recipeList.find { it.recipeBlock == input }
    }

    override fun getRecipes(): MutableList<IThermopileRecipe> = recipeList.toMutableList()

    override fun registerRecipe(recipe: IThermopileRecipe): Boolean {
        if(findRecipe(recipe.recipeBlock) == null){
            recipeList += recipe
            return true
        }
        return false
    }

    override fun removeRecipe(recipe: IThermopileRecipe?): Boolean = recipeList.remove(recipe)
}