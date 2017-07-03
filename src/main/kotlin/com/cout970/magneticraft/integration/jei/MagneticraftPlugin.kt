package com.cout970.magneticraft.integration.jei

import com.cout970.magneticraft.MOD_NAME
import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.block.Machines
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.util.resource
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.gui.elements.DrawableResource
import mezz.jei.util.Translator
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/07/2016.
 */
@JEIPlugin
class MagneticraftPlugin : IModPlugin {

    companion object {
        val CRUSHING_TABLE_ID = "magneticraft.crushing_table"
//        val TABLE_SIEVE_ID = "magneticraft.table_sieve"
//        val HYDRAULIC_PRESS_ID = "magneticraft.hydraulic_press"
//        val KILN_ID = "magneticraft.kiln"
//        val GRINDER_ID = "magneticraft.grinder"
//        val SIFTER_ID = "magneticraft.softer"
    }

    override fun register(registry: IModRegistry) {

        registry.handleRecipes(ICrushingTableRecipe::class.java, ::CrushingTableRecipeWrapper, CRUSHING_TABLE_ID)
        registry.addRecipeCatalyst(Machines.crushingTable.stack(), CRUSHING_TABLE_ID)
        registry.addRecipes(MagneticraftApi.getCrushingTableRecipeManager().recipes, CRUSHING_TABLE_ID)

    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        registry.addRecipeCategories(RecipeCategory<CrushingTableRecipeWrapper>(
                id = CRUSHING_TABLE_ID,
                backgroundTexture = "crushing_table",
                unlocalizedTitle = "text.magneticraft.jei.crushing_table",
                initFunc = { recipeLayout, recipeWrapper, _ ->
                    recipeLayout.itemStacks.init(0, true, 48, 15 - 5)
                    recipeLayout.itemStacks.init(1, false, 48, 51 - 5)
                    recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
                    recipeLayout.itemStacks.set(1, recipeWrapper.recipe.output)
                }
        ))
    }
}

class RecipeCategory<T : IRecipeWrapper>(
        val id: String,
        backgroundTexture: String,
        val unlocalizedTitle: String,
        val initFunc: (IRecipeLayout, T, IIngredients) -> Unit
) : IRecipeCategory<T>{

    private val background = DrawableResource(
            resource("textures/gui/jei/$backgroundTexture.png"),
            0, 0, 64, 64, 5, 5, 25, 25, 64, 64)


    override fun getUid(): String = id
    override fun getBackground(): IDrawable = background
    override fun getTitle(): String = Translator.translateToLocal(unlocalizedTitle)
    override fun getModName(): String = MOD_NAME

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: T, ingredients: IIngredients) {
        initFunc(recipeLayout, recipeWrapper, ingredients)
    }
}

class CrushingTableRecipeWrapper(val recipe: ICrushingTableRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(ItemStack::class.java, recipe.input)
        ingredients.setOutput(ItemStack::class.java, recipe.output)
    }
}