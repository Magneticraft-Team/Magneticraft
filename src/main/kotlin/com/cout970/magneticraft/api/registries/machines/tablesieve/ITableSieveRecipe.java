package com.cout970.magneticraft.api.registries.machines.tablesieve;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ITableSieveRecipe {

    /**
     * The input stack of this recipe
     * The stacksize is ignored
     * WARNING: this should return a COPY of the input not the original instance of the input
     *
     * @return the stack used to find this recipe
     */
    ItemStack getInput();

    /**
     * The primary output of this recipe
     * The recipe will always return this stack as output
     * WARNING: this should return a COPY of the output not the original instance of the output
     *
     * @return The primary output of this recipe
     */
    ItemStack getPrimaryOutput();

    /**
     * The primary secondary of this recipe
     * The recipe will return this stack as output if, and only if, a Random number between 0 and 1 is less
     * that the probability of this recipe
     * WARNING: this should return a COPY of the output not the original instance of the output
     *
     * @return The secondary output of this recipe
     */
    ItemStack getSecondaryOutput();

    /**
     * @return The probability to get the secondary output
     */
    float getProbability();

    /**
     * Checks if this recipes has the same input as the given argument
     * @param input the item to test if this recipe can be made from it
     * @return true if the item matches the input of this recipe, false otherwise
     */
    boolean matches(ItemStack input);
}
