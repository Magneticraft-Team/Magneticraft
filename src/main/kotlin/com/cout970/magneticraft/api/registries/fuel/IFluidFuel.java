package com.cout970.magneticraft.api.registries.fuel;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public interface IFluidFuel {

    /**
     * @return fluid stack representing this fuel
     */
    @NotNull
    FluidStack getFluid();

    /**
     * @return total time 1 bucket (1000mB) of this fuel should burn in ticks
     */
    int getTotalBurningTime();

    /**
     * @return energy in FE that this fuel generates per tick while burning
     */
    double getPowerPerCycle();

    /**
     * Check if a given fluid stack is a valid representation of this fuel
     *
     * @return true if the given stack is equivalent to the fuel fluid, false otherwise
     */
    boolean matches(@NotNull FluidStack stack);
}

