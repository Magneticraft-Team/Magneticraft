package com.cout970.magneticraft.api.registries.machines.tablesieve;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ITableSieveRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     * @param input the input to check the recipes
     * @return the recipes that matches the input or null if none matches the input
     */
    ITableSieveRecipe findRecipe(ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<ITableSieveRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     * @param recipe The recipe to register
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(ITableSieveRecipe recipe);

    /**
     * Creates a default recipe
     * @param input the input stack
     * @param primaryOutput the output stack that is always returned by the recipe
     * @param secondaryOutput the output stack that is return by the recipe bases on the probability of the recipe
     * @param prob the probability of the recipe
     * @param oreDict if ore dictionary should be used to check the inputs
     * @return the new recipe
     */
    ITableSieveRecipe createRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, float prob, boolean oreDict);
}
