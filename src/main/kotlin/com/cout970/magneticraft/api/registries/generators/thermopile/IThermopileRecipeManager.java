package com.cout970.magneticraft.api.registries.generators.thermopile;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Created by cout970 on 2017/08/28.
 */
public interface IThermopileRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    IThermopileRecipe findRecipe(@Nullable IBlockState input);

    /**
     * The list with all registered recipes
     */
    List<IThermopileRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IThermopileRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(IThermopileRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param state the input blockstate used for indexing
     * @param temperature the temperature of the block in kelvin
     * @param conductivity the conductivity of the block in Watts / (meter * kelvin) (used to make hot things less OP)
     *
     * @return the new recipe
     */
    IThermopileRecipe createRecipe(IBlockState state, float temperature, float conductivity);
}
