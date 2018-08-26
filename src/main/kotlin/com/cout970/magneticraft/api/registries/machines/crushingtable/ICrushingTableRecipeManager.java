package com.cout970.magneticraft.api.registries.machines.crushingtable;

import java.util.List;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ICrushingTableRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    ICrushingTableRecipe findRecipe(ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<ICrushingTableRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(ICrushingTableRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(ICrushingTableRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input item and required stackSize
     * @param output the output stack
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    ICrushingTableRecipe createRecipe(ItemStack input, ItemStack output, boolean oreDict);
}
