package com.cout970.magneticraft.api.registries.machines.crushingtable;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ICrushingTableRecipe {

    boolean useOreDictionaryEquivalencies();

    /**
     * The stack used to determine if an item can be used for this recipe or not Note: this will return a COPY of the
     * input not the original instance of the input
     *
     * @return The input of the recipe
     */
    ItemStack getInput();

    /**
     * The result of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The output of the recipe
     */
    ItemStack getOutput();

    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param input the item to test if this recipe can be made from it
     *
     * @return true if the item matches the input of this recipe, false otherwise
     */
    boolean matches(ItemStack input);
}
