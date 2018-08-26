package com.cout970.magneticraft.api.registries.machines.grinder;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 22/08/2016.
 */
public interface IGrinderRecipe {

    /**
     * Whether or not this recipe use the OreDictionary to check if the input is valid
     *
     * @return if the OreDictionary will be used to compare items
     */
    boolean useOreDictionaryEquivalencies();

    /**
     * The item, metadata and nbt used to determine if an item can be used for this recipe or not Note: stacksize is
     * used to check the recipe Note: this will return a COPY of the input not the original instance of the input
     *
     * @return The input of the recipe
     */
    ItemStack getInput();

    /**
     * The result of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The output of the recipe
     */
    ItemStack getPrimaryOutput();

    /**
     * The secondary result of this recipe Note: this will return a COPY of the output not the original instance of the
     * output
     *
     * @return The secondary output of the recipe
     */
    ItemStack getSecondaryOutput();


    /**
     * Fractional chance to obtain secondary output
     */
    float getProbability();

    /**
     * The amount of ticks needed to complete the recipe
     */
    float getDuration();

    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param input the item to test if this recipe can be made from it
     *
     * @return true if the item matches the input of this recipe, false otherwise
     */
    boolean matches(ItemStack input);
}
