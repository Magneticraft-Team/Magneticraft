package com.cout970.magneticraft.api.registries.machines.refinery;

import java.util.List;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public interface IRefineryRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    IRefineryRecipe findRecipe(FluidStack input);

    /**
     * The list with all registered recipes
     */
    List<IRefineryRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IRefineryRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(IRefineryRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input item
     * @param output0 the primary output
     * @param output1 the secondary output
     * @param output2 the tertiary output
     *
     * @return the new recipe
     */
    IRefineryRecipe createRecipe(FluidStack input, @Nullable FluidStack output0, @Nullable FluidStack output1,
                                 @Nullable FluidStack output2, float duration);
}
