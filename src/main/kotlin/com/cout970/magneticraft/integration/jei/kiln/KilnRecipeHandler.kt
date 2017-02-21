package com.cout970.magneticraft.integration.jei.kiln

import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import com.cout970.magneticraft.integration.jei.kiln.KilnRecipeWrapper
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 24/08/2016.
 */
object KilnRecipeHandler : IRecipeHandler<IKilnRecipe> {

    override fun getRecipeCategoryUid(recipe: IKilnRecipe): String = JEIPlugin.KILN_ID

    override fun getRecipeCategoryUid(): String = recipeCategoryUid

    override fun getRecipeClass(): Class<IKilnRecipe> = IKilnRecipe::class.java

    override fun isRecipeValid(recipe: IKilnRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: IKilnRecipe): IRecipeWrapper = KilnRecipeWrapper(recipe)
}