package com.cout970.magneticraft.api.internal.registries.machines.tablesieve

import net.minecraft.item.ItemStack

import java.util.ArrayList
import java.util.LinkedList

/**
 * Created by cout970 on 16/06/2016.
 */
object TableSieveRecipeManager {

    private val recipes = LinkedList<TableSieveRecipe>()

    fun findRecipe(stack: ItemStack): TableSieveRecipe? {
        for (rec in recipes) {
            if (rec.matches(stack)) {
                return rec
            }
        }
        return null
    }

    fun registerRecipe(recipe: TableSieveRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return false
        }
        recipes.add(recipe)
        return true
    }

    fun getRecipes(): List<TableSieveRecipe> {
        return ArrayList(recipes)
    }
}
