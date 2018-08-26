package com.cout970.magneticraft.api.registries.machines.kiln;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 22/08/2016.
 */
public interface IKilnRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    IKilnRecipe findRecipe(ItemStack input);

    /**
     * The lists with all registered recipes
     */
    List<IKilnRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(IKilnRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input item and required stackSize
     * @param output the output stack
     * @param duration the number of multiples of the update frequencyneeded to craft the result
     * @param minTemp the minimum temperature at which the recipe will occur
     * @param maxTemp the maximum temperature at which the recipe will occur
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    IKilnRecipe createRecipe(ItemStack input, ItemStack output, int duration, double minTemp, double maxTemp,
                             boolean oreDict);

    /**
     * Creates a default recipe
     *
     * @param input the input stack
     * @param output the output block
     * @param duration the number of multiples of the update frequency needed to craft the result
     * @param minTemp the minimum temperature at which the recipe will occur
     * @param maxTemp the maximum temperature at which the recipe will occur
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    IKilnRecipe createRecipe(ItemStack input, IBlockState output, int duration, double minTemp, double maxTemp,
                             boolean oreDict);
}
