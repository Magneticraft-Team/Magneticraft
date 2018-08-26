package com.cout970.magneticraft.api.registries.machines.gasificationunit;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IGasificationUnitRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    IGasificationUnitRecipe findRecipe(@NotNull ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<IGasificationUnitRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(@NotNull IGasificationUnitRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(@NotNull IGasificationUnitRecipe recipe);

    /**
     * Creates a default recipe
     * <p>
     * A item, fluid or both can be specified as outputs, but both cannot be empty
     *
     * @param input the input item
     * @param itemOutput the option output item
     * @param fluidOutput the option output fluid
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    IGasificationUnitRecipe createRecipe(@NotNull ItemStack input, @NotNull ItemStack itemOutput,
                                         FluidStack fluidOutput,
                                         float time, float minTemperature, boolean oreDict);
}
