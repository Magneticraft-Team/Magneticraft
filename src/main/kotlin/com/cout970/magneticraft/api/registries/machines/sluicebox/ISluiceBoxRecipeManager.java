package com.cout970.magneticraft.api.registries.machines.sluicebox;

import kotlin.Pair;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ISluiceBoxRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     * @param input the input to check the recipes
     * @return the recipes that matches the input or null if none matches the input
     */
    ISluiceBoxRecipe findRecipe(ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<ISluiceBoxRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     * @param recipe The recipe to register
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(ISluiceBoxRecipe recipe);

    /**
     * Removes a recipe from the registry
     * @param recipe The recipe to remove
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(ISluiceBoxRecipe recipe);

    /**
     * Creates a default recipe
     * @param input the input stack
     * @param primaryOutput the output stack that is always returned by the recipe
     * @param secondaryOutput the output stacks and probability of every item to be created by the sluice box
     * @param oreDict if ore dictionary should be used to check the inputs
     * @return the new recipe
     */
    ISluiceBoxRecipe createRecipe(ItemStack input, ItemStack primaryOutput, List<Pair<ItemStack, Float>> secondaryOutput, boolean oreDict);
}
