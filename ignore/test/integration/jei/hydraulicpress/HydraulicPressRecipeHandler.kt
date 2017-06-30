package integration.jei.hydraulicpress

import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import com.cout970.magneticraft.integration.jei.JEIPlugin
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by cout970 on 24/08/2016.
 */
object HydraulicPressRecipeHandler : IRecipeHandler<IHydraulicPressRecipe> {

    override fun getRecipeCategoryUid(recipe: IHydraulicPressRecipe): String = JEIPlugin.HYDRAULIC_PRESS_ID

    override fun getRecipeCategoryUid(): String = recipeCategoryUid

    override fun getRecipeClass(): Class<IHydraulicPressRecipe> = IHydraulicPressRecipe::class.java

    override fun isRecipeValid(recipe: IHydraulicPressRecipe): Boolean = true

    override fun getRecipeWrapper(recipe: IHydraulicPressRecipe): IRecipeWrapper = HydraulicPressRecipeWrapper(recipe)
}