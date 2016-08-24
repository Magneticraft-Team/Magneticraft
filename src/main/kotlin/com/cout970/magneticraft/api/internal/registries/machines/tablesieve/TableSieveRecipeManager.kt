package com.cout970.magneticraft.api.internal.registries.machines.tablesieve

import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipe
import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipeManager
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 16/06/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getTableSieveRecipeManager() instead
 */
object TableSieveRecipeManager : ITableSieveRecipeManager {

    private val recipes = LinkedList<ITableSieveRecipe>()

    override fun findRecipe(stack: ItemStack): ITableSieveRecipe? {
        for (rec in recipes) {
            if (rec.matches(stack)) {
                return rec
            }
        }
        return null
    }

    override fun registerRecipe(recipe: ITableSieveRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return false
        }
        recipes.add(recipe)
        return true
    }

    override fun getRecipes(): List<ITableSieveRecipe> = Collections.synchronizedList(recipes)

    override fun createRecipe(input: ItemStack, primaryOutput: ItemStack, secondaryOutput: ItemStack, prob: Float, oreDict: Boolean): ITableSieveRecipe {
        return TableSieveRecipe(input, primaryOutput, secondaryOutput, prob, oreDict)
    }
}
