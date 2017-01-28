package com.cout970.magneticraft.api.internal.registries.machines.heatrecipes

import com.cout970.magneticraft.api.registries.machines.heatrecipes.IIceboxRecipe
import com.cout970.magneticraft.api.registries.machines.heatrecipes.IIceboxRecipeManager
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.*

/**
 * Created by Yurgen on 16/06/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getHeatExchangerRecipeManager() instead
 */
object IceboxRecipeManager : IIceboxRecipeManager {

    private val recipes = LinkedList<IIceboxRecipe>()
    private val fluids = mutableSetOf<Fluid>()

    override fun findRecipe(stack: ItemStack): IIceboxRecipe? {
        for (rec in recipes) {
            if (rec.matches(stack)) {
                return rec
            }
        }
        return null
    }

    override fun findRecipeReverse(stack: FluidStack): IIceboxRecipe? {
        for (rec in recipes) {
            if (rec.matchesReverse(stack)) {
                return rec
            }
        }
        return null
    }

    override fun registerRecipe(recipe: IIceboxRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return false
        }
        recipes.add(recipe)
        fluids.add(recipe.output.fluid)
        return true
    }

    override fun getValidFluids(): List<Fluid> {
        return fluids.toList()
    }

    override fun getRecipes(): List<IIceboxRecipe> = Collections.synchronizedList(recipes)

    override fun createRecipe(input: ItemStack, output: FluidStack, heat: Long, specificHeat: Double, minTemp: Double, maxTemp: Double, reverse: Boolean): IIceboxRecipe {
        return IceboxRecipe(input, output, heat, specificHeat, minTemp, maxTemp, reverse)
    }
}
