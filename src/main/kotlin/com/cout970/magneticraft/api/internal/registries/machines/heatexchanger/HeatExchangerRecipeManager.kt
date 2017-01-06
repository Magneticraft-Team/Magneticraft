package com.cout970.magneticraft.api.internal.registries.machines.heatexchanger

import com.cout970.magneticraft.api.registries.machines.heatexchanger.IHeatExchangerRecipe
import com.cout970.magneticraft.api.registries.machines.heatexchanger.IHeatExchangerRecipeManager
import net.minecraftforge.fluids.FluidStack
import java.util.*

/**
 * Created by Yurgen on 16/06/2016.
 */
/**
 * Internal class only please use MagneticraftApi.getHeatExchangerRecipeManager() instead
 */
object HeatExchangerRecipeManager : IHeatExchangerRecipeManager {

    private val recipes = LinkedList<IHeatExchangerRecipe>()

    override fun findRecipe(stack: FluidStack): IHeatExchangerRecipe? {
        for (rec in recipes) {
            if (rec.matches(stack)) {
                return rec
            }
        }
        return null
    }

    override fun registerRecipe(recipe: IHeatExchangerRecipe): Boolean {
        if (findRecipe(recipe.input) != null) {
            return false
        }
        recipes.add(recipe)
        return true
    }

    override fun getRecipes(): List<IHeatExchangerRecipe> = Collections.synchronizedList(recipes)

    override fun createRecipe(input: FluidStack, output: FluidStack, heat: Long, minTemp: Double, maxTemp: Double, reverseLow: Boolean, reverseHigh: Boolean): IHeatExchangerRecipe {
        return HeatExchangerRecipe(input, output, heat, minTemp, maxTemp, reverseLow, reverseHigh)
    }
}
