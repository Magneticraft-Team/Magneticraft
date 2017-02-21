package com.cout970.magneticraft.integration.jei.kiln

import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 24/08/2016.
 */
class KilnRecipeWrapper(val recipe: IKilnRecipe) : IRecipeWrapper {

    override fun drawAnimations(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int) = Unit

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) = Unit

    override fun getTooltipStrings(mouseX: Int, mouseY: Int): MutableList<String>? = mutableListOf()

    override fun getFluidInputs(): MutableList<FluidStack>? = mutableListOf()

    override fun handleClick(minecraft: Minecraft, mouseX: Int, mouseY: Int, mouseButton: Int): Boolean = false

    override fun getOutputs(): MutableList<Any?>? = mutableListOf(recipe.itemOutput, recipe.blockOutput)

    override fun getFluidOutputs(): MutableList<FluidStack>? = mutableListOf()

    override fun getInputs(): MutableList<Any?>? = mutableListOf(recipe.input)

    override fun getIngredients(ingredients: IIngredients?) {}
}