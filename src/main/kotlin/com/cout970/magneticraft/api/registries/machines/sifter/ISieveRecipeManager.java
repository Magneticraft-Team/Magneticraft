package com.cout970.magneticraft.api.registries.machines.sifter;

import java.util.List;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ISieveRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    ISieveRecipe findRecipe(ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<ISieveRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(ISieveRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return if the removal has ended successfully
     */
    boolean removeRecipe(ISieveRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input stack
     * @param primary the output stack that is always returned by the recipe
     * @param secondary a pair containing the secondary output and the probability of generating it
     * @param tertiary a pair containing the tertiary output and the probability of generating it
     * @param duration Number of ticks for one crafting operation
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    ISieveRecipe createRecipe(ItemStack input, ItemStack primary, float primaryChance, ItemStack secondary,
                              float secondaryChance, ItemStack tertiary, float tertiaryChance, float duration,
                              boolean oreDict);
}
