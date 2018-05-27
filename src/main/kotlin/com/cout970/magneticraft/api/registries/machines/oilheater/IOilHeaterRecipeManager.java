package com.cout970.magneticraft.api.registries.machines.oilheater;

import java.util.List;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public interface IOilHeaterRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    IOilHeaterRecipe findRecipe(FluidStack input);

    /**
     * The list with all registered recipes
     */
    List<IOilHeaterRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IOilHeaterRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(IOilHeaterRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input item
     * @param output the output stack
     *
     * @return the new recipe
     */
    IOilHeaterRecipe createRecipe(FluidStack input, FluidStack output, float duration, float minTemperature);
}
