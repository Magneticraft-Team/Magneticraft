package com.cout970.magneticraft.api.registries.generators.thermopile;

import java.util.List;
import net.minecraft.block.Block;

/**
 * Created by cout970 on 2017/08/28.
 */
public interface IThermopileRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     * @param input the input to check the recipes
     * @return the recipes that matches the input or null if none matches the input
     */
    IThermopileRecipe findRecipe(Block input);

    /**
     * The list with all registered recipes
     */
    List<IThermopileRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     * @param recipe The recipe to register
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IThermopileRecipe recipe);

    /**
     * Removes a recipe from the registry
     * @param recipe The recipe to remove
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(IThermopileRecipe recipe);
}
