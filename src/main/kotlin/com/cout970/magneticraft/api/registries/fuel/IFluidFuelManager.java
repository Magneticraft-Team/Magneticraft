package com.cout970.magneticraft.api.registries.fuel;

import java.util.List;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IFluidFuelManager {

    /**
     * Finds a fuel from the registered fuels that matches the given fluidStack
     */
    @Nullable
    IFluidFuel findFuel(@NotNull FluidStack fluidStack);

    /**
     * The list with all registered fuels
     */
    List<IFluidFuel> getFuels();

    /**
     * Register a fuel if is not already registered
     *
     * @param recipe The fuel to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerFuel(@NotNull IFluidFuel recipe);

    /**
     * Removes a fuel from the registry
     */
    boolean removeFuel(@NotNull IFluidFuel recipe);

    /**
     * Create a FluidFuel using the default implementation
     *
     * @param fluidStack fluid that represents the fuel
     * @param burningTime amount of ticks that takes 1 bucket of fuel to burn, must be bigger than 0
     * @param powerPerCycle energy in RF given by the fuel evey tick while burning, must be bigger than 0
     *
     * @return A default implementation of IFluidFuel
     */
    IFluidFuel createFuel(@NotNull FluidStack fluidStack, int burningTime, double powerPerCycle);
}
