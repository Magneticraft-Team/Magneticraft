package com.cout970.magneticraft.api.registries.machines.grinder;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by cout970 on 22/08/2016.
 */
public interface IGrinderRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     * @return the recipes that matches the input or null if none matches the input
     */
    IGrinderRecipe findRecipe(ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<IGrinderRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IGrinderRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input   the input item and required stackSize
     * @param output0  the primary output stack
     * @param output1  the secondary output stack
     * @param prob   the fractional chance to obtain secondary output
     * @param ticks   the amount of ticks needed to craft the result
     * @param oreDict if ore dictionary should be used to check the inputs
     * @return the new recipe
     */
    IGrinderRecipe createRecipe(ItemStack input, ItemStack output0, ItemStack output1, float prob, float ticks, boolean oreDict);
}
