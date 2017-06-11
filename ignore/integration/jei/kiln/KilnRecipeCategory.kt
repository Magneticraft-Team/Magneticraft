package com.cout970.magneticraft.integration.jei.kiln

import com.cout970.magneticraft.integration.jei.JEIPlugin
import com.cout970.magneticraft.integration.jei.kiln.KilnRecipeWrapper
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.toCelsius
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.gui.DrawableResource
import mezz.jei.util.Translator
import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 24/08/2016.
 */
object KilnRecipeCategory : IRecipeCategory<KilnRecipeWrapper> {

    private val title = Translator.translateToLocal("text.magneticraft.jei.kiln")
    private val background = DrawableResource(resource("textures/gui/jei/gui.png"), 128, 0, 64, 64, 5, 5, 25, 25)

    override fun drawAnimations(minecraft: Minecraft) {}

    override fun drawExtras(minecraft: Minecraft) {}

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: KilnRecipeWrapper) {}

    override fun getTitle(): String = title

    override fun getUid(): String = JEIPlugin.KILN_ID

    override fun getBackground(): IDrawable = background

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: KilnRecipeWrapper, ingredients: IIngredients?) {
        recipeLayout.itemStacks.init(0, true, 47, 15 - 5)
        recipeLayout.itemStacks.init(1, false, 47, 51 - 5)
        recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
        if (recipeWrapper.recipe.isBlockRecipe) recipeLayout.itemStacks.set(1, recipeWrapper.recipe.blockOutputAsItem!!)
        else recipeLayout.itemStacks.set(1, recipeWrapper.recipe.itemOutput!!)
        recipeLayout.itemStacks.addTooltipCallback({ slot, _, _, list ->
            if (slot == 1) {
                val str1 = "Min: %.1fC ".format(recipeWrapper.recipe.minTemp.toCelsius())
                val str2 = "Max: %.1fC".format(recipeWrapper.recipe.maxTemp.toCelsius())
                list.add(str1 + str2)
            }
        })
    }

    override fun getIcon(): IDrawable? = null

    override fun getTooltipStrings(mouseX: Int, mouseY: Int): MutableList<String> = mutableListOf()
}