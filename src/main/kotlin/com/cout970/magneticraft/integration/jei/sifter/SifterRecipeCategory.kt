package com.cout970.magneticraft.integration.jei.sifter

import com.cout970.magneticraft.integration.jei.JEIPlugin
import com.cout970.magneticraft.integration.jei.sifter.SifterRecipeWrapper
import com.cout970.magneticraft.util.resource
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.gui.DrawableResource
import mezz.jei.util.Translator
import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 23/07/2016.
 */
object SifterRecipeCategory : IRecipeCategory<SifterRecipeWrapper> {

    private val title = Translator.translateToLocal("text.magneticraft.jei.sifter")
    private val background = DrawableResource(resource("textures/gui/jei/gui.png"), 64, 0, 64, 64, 5, 5, 25, 25)

    override fun drawAnimations(minecraft: Minecraft) {}

    override fun drawExtras(minecraft: Minecraft) {}

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: SifterRecipeWrapper) {}

    override fun getTitle(): String = title

    override fun getUid(): String = JEIPlugin.SIFTER_ID

    override fun getBackground(): IDrawable = background

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: SifterRecipeWrapper, ingredients: IIngredients?) {
        recipeLayout.itemStacks.init(0, true, 48, 15 - 5)
        recipeLayout.itemStacks.init(1, false, 48 - 18, 51 - 5)
        recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
        recipeLayout.itemStacks.set(1, recipeWrapper.recipe.primary)
        if (recipeWrapper.recipe.secondaryChance > 0f) {
            recipeLayout.itemStacks.init(2, false, 48, 51 - 5)
            recipeLayout.itemStacks.set(2, recipeWrapper.recipe.secondary)
            recipeLayout.itemStacks.addTooltipCallback { slot, _, _, list ->
                if (slot == 2) list.add("Probability: %.1f%%".format(recipeWrapper.recipe.secondaryChance * 100))
            }
        }
        if (recipeWrapper.recipe.tertiaryChance > 0f) {
            recipeLayout.itemStacks.init(3, false, 48 + 18, 51 - 5)
            recipeLayout.itemStacks.set(3, recipeWrapper.recipe.tertiary)
            recipeLayout.itemStacks.addTooltipCallback { slot, _, _, list ->
                if (slot == 3) list.add("Probability: %.1f%%".format(recipeWrapper.recipe.tertiaryChance * 100))
            }
        }
    }

    override fun getIcon(): IDrawable? = null

    override fun getTooltipStrings(mouseX: Int, mouseY: Int): MutableList<String> = mutableListOf()
}