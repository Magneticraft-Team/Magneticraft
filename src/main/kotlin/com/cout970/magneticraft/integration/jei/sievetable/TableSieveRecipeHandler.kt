package com.cout970.magneticraft.integration.jei.sievetable

import com.cout970.magneticraft.api.registries.machines.tablesieve.TableSieveRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 23/07/2016.
 */
object TableSieveRecipeHandler : IRecipeHandler<TableSieveRecipe> {

    override fun getRecipeClass(): Class<TableSieveRecipe> = TableSieveRecipe::class.java

    override fun getRecipeCategoryUid(): String = JEIPlugin.TABLE_SIEVE_ID

    override fun getRecipeCategoryUid(recipe: TableSieveRecipe): String = recipeCategoryUid

    override fun isRecipeValid(recipe: TableSieveRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: TableSieveRecipe): IRecipeWrapper = TableSieveRecipeWrapper(recipe)
}