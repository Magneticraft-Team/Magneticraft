package com.cout970.magneticraft.api.registries.machines.hydraulicpress;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 22/08/2016.
 */
public interface IHydraulicPressRecipe {

    /**
     * Whether or not this recipe use the OreDictionary to check if the input is valid
     *
     * @return if the OreDictionary will be used to compare items
     */
    boolean useOreDictionaryEquivalencies();

    /**
     * The of the machine needed to craft this item
     */
    HydraulicPressMode getMode();

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
    ItemStack getOutput();

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
