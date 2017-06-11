package com.cout970.magneticraft.integration.jei.sievetable

import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 23/07/2016.
 */
object TableSieveRecipeHandler : IRecipeHandler<ITableSieveRecipe> {

    override fun getRecipeClass(): Class<ITableSieveRecipe> = ITableSieveRecipe::class.java

    override fun getRecipeCategoryUid(): String = JEIPlugin.TABLE_SIEVE_ID

    override fun getRecipeCategoryUid(recipe: ITableSieveRecipe): String = recipeCategoryUid

    override fun isRecipeValid(recipe: ITableSieveRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: ITableSieveRecipe): IRecipeWrapper = TableSieveRecipeWrapper(recipe)
}