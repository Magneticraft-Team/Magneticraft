package com.cout970.magneticraft.api.registries.machines.refinery;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public interface IRefineryRecipe {

    /**
     * The stack used to determine if an item can be used for this recipe or not Note: this will return a COPY of the
     * input not the original instance of the input
     *
     * @return The input of the recipe
     */
    FluidStack getInput();

    /**
     * The result of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The output of the recipe
     */
    @Nullable
    FluidStack getOutput0();

    /**
     * The result of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The output of the recipe
     */
    @Nullable
    FluidStack getOutput1();

    /**
     * The result of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The output of the recipe
     */
    @Nullable
    FluidStack getOutput2();

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
    boolean matches(@Nullable FluidStack input);
}
