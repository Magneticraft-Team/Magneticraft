package com.cout970.magneticraft.integration.jei.hydraulicpress

import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 24/08/2016.
 */
class HydraulicPressRecipeWrapper(val recipe : IHydraulicPressRecipe ) : IRecipeWrapper {

    override fun drawAnimations(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int) = Unit

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) = Unit

    override fun getTooltipStrings(mouseX: Int, mouseY: Int): MutableList<String>? = mutableListOf()

    override fun getFluidInputs(): MutableList<FluidStack>? = mutableListOf()

    override fun handleClick(minecraft: Minecraft, mouseX: Int, mouseY: Int, mouseButton: Int): Boolean = false

    override fun getOutputs(): MutableList<Any?>? = mutableListOf(recipe.output)

    override fun getFluidOutputs(): MutableList<FluidStack>? = mutableListOf()

    override fun getInputs(): MutableList<Any?>? = mutableListOf(recipe.input)

    override fun getIngredients(ingredients: IIngredients?) {}
}