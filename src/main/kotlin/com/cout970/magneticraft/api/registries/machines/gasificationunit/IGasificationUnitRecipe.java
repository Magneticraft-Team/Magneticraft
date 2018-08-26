package com.cout970.magneticraft.api.registries.machines.gasificationunit;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IGasificationUnitRecipe {

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
     * @return The item output of the recipe
     */
    ItemStack getItemOutput();

    /**
     * The result of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The fluid output of the recipe
     */
    @Nullable
    FluidStack getFluidOutput();

    /**
     * The amount of ticks needed to complete the recipe
     */
    float getDuration();

    /**
     * The minimum temperature to start processing
     */
    float minTemperature();

    /**
     * Whether or not this recipe use the OreDictionary to check if the input is valid
     *
     * @return if the OreDictionary will be used to compare items
     */
    boolean useOreDictionaryEquivalencies();

    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param input the item to test if this recipe can be made from it
     *
     * @return true if the item matches the input of this recipe, false otherwise
     */
    boolean matches(@NotNull ItemStack input);
}
