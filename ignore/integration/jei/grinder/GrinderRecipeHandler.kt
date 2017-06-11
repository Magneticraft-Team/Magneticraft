package com.cout970.magneticraft.integration.jei.grinder

import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import com.cout970.magneticraft.integration.jei.grinder.GrinderRecipeWrapper
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 23/07/2016.
 */
object GrinderRecipeHandler : IRecipeHandler<IGrinderRecipe> {

    override fun getRecipeCategoryUid(): String = JEIPlugin.GRINDER_ID

    override fun getRecipeCategoryUid(recipe: IGrinderRecipe): String = recipeCategoryUid

    override fun isRecipeValid(recipe: IGrinderRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: IGrinderRecipe): IRecipeWrapper {
        return GrinderRecipeWrapper(recipe)
    }

    override fun getRecipeClass(): Class<IGrinderRecipe> = IGrinderRecipe::class.java
}