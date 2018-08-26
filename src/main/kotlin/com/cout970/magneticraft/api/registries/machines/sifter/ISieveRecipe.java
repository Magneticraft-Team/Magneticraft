package com.cout970.magneticraft.api.registries.machines.sifter;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ISieveRecipe {

    /**
     * Whether or not this recipe use the OreDictionary to check if the input is valid
     *
     * @return if the OreDictionary will be used to compare items
     */
    boolean useOreDictionaryEquivalencies();

    /**
     * The input stack of this recipe The stacksize is ignored Note: this will return a COPY of the input not the
     * original instance of the input
     *
     * @return the stack used to find this recipe
     */
    ItemStack getInput();

    /**
     * The primary output of this recipe The recipe will always return this stack as output Note: this will return a
     * COPY of the output not the original instance of the output
     *
     * @return The primary output of this recipe
     */
    ItemStack getPrimary();

    /**
     * The secondary output of this recipe The recipe will return this stack as output if, and only if, a Random number
     * between 0 and 1 is less that the probability of this recipe Note: this will return a COPY of the output not the
     * original instance of the output
     *
     * @return The secondary output of this recipe
     */
    ItemStack getSecondary();

    /**
     * The tertiary output of this recipe The recipe will return this stack as output if, and only if, a Random number
     * between 0 and 1 is less that the probability of this recipe Note: this will return a COPY of the output not the
     * original instance of the output
     *
     * @return The secondary output of this recipe
     */
    ItemStack getTertiary();

    /**
     * @return The probability to get the primary output
     */
    float getPrimaryChance();

    /**
     * @return The probability to get the secondary output
     */
    float getSecondaryChance();

    /**
     * @return The probability to get the tertiary output
     */
    float getTertiaryChance();

    /**
     * @return The time to perform one crafting operation
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
