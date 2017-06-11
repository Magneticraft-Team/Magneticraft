package com.cout970.magneticraft.integration.jei.sifter

import com.cout970.magneticraft.api.registries.machines.sifter.ISifterRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import com.cout970.magneticraft.integration.jei.sifter.SifterRecipeWrapper
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 23/07/2016.
 */
object SifterRecipeHandler : IRecipeHandler<ISifterRecipe> {

    override fun getRecipeClass(): Class<ISifterRecipe> = ISifterRecipe::class.java

    override fun getRecipeCategoryUid(): String = JEIPlugin.SIFTER_ID

    override fun getRecipeCategoryUid(recipe: ISifterRecipe): String = recipeCategoryUid

    override fun isRecipeValid(recipe: ISifterRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: ISifterRecipe): IRecipeWrapper = SifterRecipeWrapper(recipe)
}