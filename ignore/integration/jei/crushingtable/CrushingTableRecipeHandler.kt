package com.cout970.magneticraft.integration.jei.crushingtable

import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 23/07/2016.
 */
object CrushingTableRecipeHandler : IRecipeHandler<ICrushingTableRecipe> {

    override fun getRecipeCategoryUid(): String = JEIPlugin.CRUSHING_TABLE_ID

    override fun getRecipeCategoryUid(recipe: ICrushingTableRecipe): String = recipeCategoryUid

    override fun isRecipeValid(recipe: ICrushingTableRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: ICrushingTableRecipe): IRecipeWrapper {
        return CrushingTableRecipeWrapper(recipe)
    }

    override fun getRecipeClass(): Class<ICrushingTableRecipe> = ICrushingTableRecipe::class.java
}