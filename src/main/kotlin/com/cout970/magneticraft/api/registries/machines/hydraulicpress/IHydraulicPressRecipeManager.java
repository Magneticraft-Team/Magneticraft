package com.cout970.magneticraft.api.registries.machines.hydraulicpress;

import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 22/08/2016.
 */
public interface IHydraulicPressRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     * @param mode the current mode of the hydraulic press
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    IHydraulicPressRecipe findRecipe(ItemStack input, HydraulicPressMode mode);

    /**
     * The list with all registered recipes
     */
    List<IHydraulicPressRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IHydraulicPressRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return if the removal has ended successfully
     */
    boolean removeRecipe(IHydraulicPressRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input item and required stackSize
     * @param output the output stack
     * @param ticks the amount of ticks needed to craft the result
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    IHydraulicPressRecipe createRecipe(ItemStack input, ItemStack output, float ticks,
                                       HydraulicPressMode mode, boolean oreDict);
}
