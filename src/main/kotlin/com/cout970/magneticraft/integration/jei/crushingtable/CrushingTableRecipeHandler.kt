package com.cout970.magneticraft.integration.jei.crushingtable

import com.cout970.magneticraft.integration.jei.JEIPlugin
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 23/07/2016.
 */
object CrushingTableRecipeHandler : IRecipeHandler<CrushingTableRecipeWrapper> {

    override fun getRecipeCategoryUid(): String = JEIPlugin.CRUSHING_TABLE_ID

    override fun getRecipeCategoryUid(recipe: CrushingTableRecipeWrapper): String = recipeCategoryUid

    override fun isRecipeValid(recipe: CrushingTableRecipeWrapper): Boolean {
        return true
    }

    override fun getRecipeWrapper(recipe: CrushingTableRecipeWrapper): IRecipeWrapper {
        return recipe
    }

    override fun getRecipeClass(): Class<CrushingTableRecipeWrapper> = CrushingTableRecipeWrapper::class.java
}