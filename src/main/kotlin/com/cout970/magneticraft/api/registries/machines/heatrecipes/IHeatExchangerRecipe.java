package com.cout970.magneticraft.api.registries.machines.heatrecipes;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface IHeatExchangerRecipe {

    /**
     * The input stack of this recipe The stacksize is ignored Note: this will return a COPY of the input not the
     * original instance of the input
     *
     * @return the stack used to find this recipe
     */
    FluidStack getInput();

    /**
     * The output of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The primary output of this recipe
     */
    FluidStack getOutput();

    /**
     * The heat change of this recipe
     *
     * @return the heat generated (or consumed, if negative) by this recipe
     */
    long getHeat();

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
     * If the reaction is reversible at low temperatures
     *
     * @return True if the recipe will occur in reverse if the temperature is below the minimum temperature
     */
    boolean getReverseLow();

    /**
     * If the reaction is reversible at high temperatures
     *
     * @return True if the recipe will occur in reverse if the temperature is above the maximum temperature
     */
    boolean getReverseHigh();

    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param input the fluid to test if this recipe can be made from it
     *
     * @return true if the fluid matches the input of this recipe, false otherwise
     */
    boolean matches(FluidStack input);


    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param output the fluid to test if this recipe can be made from it
     *
     * @return true if the fluid matches the input of this recipe, false otherwise
     */
    boolean matchesReverse(FluidStack output);
}
