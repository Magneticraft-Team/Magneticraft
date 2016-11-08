package com.cout970.magneticraft.api.registries.machines.kiln;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 22/08/2016.
 */
public interface IKilnRecipe {

    /**
     * The item, metadata and nbt used to determine if an item can be used for this recipe or not
     * Note: stacksize is used to check the recipe
     * WARNING: this should return a COPY of the input not the original instance of the input
     *
     * @return The input of the recipe
     */
    ItemStack getInput();

    /**
     * The result of this recipe
     * WARNING: this should return a COPY of the output not the original instance of the output
     *
     * @return The output of the recipe
     */
    ItemStack getOutput();

    /**
     * The minimum temperature required for this recipe to occur
     *
     * @return the minimum temperature at which this recipe will occur
     */
    double getMinTemp();

    /**
     * The maximum temperature required for this recipe to occur
     *
     * @return the maximum temperature at which this recipe will occur
     */
    double getMaxTemp();

    /**
     * The amount of ticks needed to complete the recipe
     */
    float getDuration();

    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param input the item to test if this recipe can be made from it
     * @return true if the item matches the input of this recipe, false otherwise
     */
    boolean matches(ItemStack input);
}