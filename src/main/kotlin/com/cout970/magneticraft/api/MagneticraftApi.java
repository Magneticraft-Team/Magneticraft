package com.cout970.magneticraft.api;

import com.cout970.magneticraft.api.internal.registries.fuel.FluidFuelManager;
import com.cout970.magneticraft.api.internal.registries.generation.OreGenerationRegistry;
import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.oilheater.OilHeaterRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.refinery.RefineryRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.sieve.SieveRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipeManager;
import com.cout970.magneticraft.api.internal.registries.tool.hammer.HammerRegistry;
import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry;
import com.cout970.magneticraft.api.multiblock.IMultiblockManager;
import com.cout970.magneticraft.api.registries.fuel.IFluidFuelManager;
import com.cout970.magneticraft.api.registries.generation.IOreGenerationRegistry;
import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipeManager;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipeManager;
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipeManager;
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipeManager;
import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipeManager;
import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipeManager;
import com.cout970.magneticraft.api.registries.tool.hammer.IHammerRegistry;
import com.cout970.magneticraft.api.registries.tool.wrench.IWrenchRegistry;
import com.cout970.magneticraft.systems.multiblocks.MultiblockManager;

/**
 * Created by cout970 on 22/08/2016.
 */
public class MagneticraftApi {

    private MagneticraftApi() {
    }

    public static IRefineryRecipeManager getRefineryRecipeManager() {
        return RefineryRecipeManager.INSTANCE;
    }

    public static IOilHeaterRecipeManager getOilHeaterRecipeManager() {
        return OilHeaterRecipeManager.INSTANCE;
    }

    public static IHydraulicPressRecipeManager getHydraulicPressRecipeManager() {
        return HydraulicPressRecipeManager.INSTANCE;
    }

    public static IGrinderRecipeManager getGrinderRecipeManager() {
        return GrinderRecipeManager.INSTANCE;
    }

    public static ISieveRecipeManager getSieveRecipeManager() {
        return SieveRecipeManager.INSTANCE;
    }

    public static IMultiblockManager getMultiblockManager() {
        return MultiblockManager.INSTANCE;
    }

    public static ISluiceBoxRecipeManager getSluiceBoxRecipeManager() {
        return SluiceBoxRecipeManager.INSTANCE;
    }

    public static ICrushingTableRecipeManager getCrushingTableRecipeManager() {
        return CrushingTableRecipeManager.INSTANCE;
    }

    public static IGasificationUnitRecipeManager getGasificationUnitRecipeManager() {
        return GasificationUnitRecipeManager.INSTANCE;
    }

    public static IThermopileRecipeManager getThermopileRecipeManager() {
        return ThermopileRecipeManager.INSTANCE;
    }

    public static IOreGenerationRegistry getOreGenerationRegistry() {
        return OreGenerationRegistry.INSTANCE;
    }

    public static IHammerRegistry getHammerRegistry() {
        return HammerRegistry.INSTANCE;
    }

    public static IWrenchRegistry getWrenchRegistry() {
        return WrenchRegistry.INSTANCE;
    }

    public static IFluidFuelManager getFluidFuelManager() {
        return FluidFuelManager.FLUID_FUEL_MANAGER;
    }
}