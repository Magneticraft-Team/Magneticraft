package com.cout970.magneticraft.api;

import com.cout970.magneticraft.api.internal.registries.generation.OreGenerationRegistry;
import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.heatrecipes.IceboxRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.kiln.KilnRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.sifter.SifterRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipeManager;
import com.cout970.magneticraft.api.registries.generation.IOreGenerationRegistry;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipeManager;
import com.cout970.magneticraft.api.registries.machines.heatrecipes.IIceboxRecipeManager;
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipeManager;
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sifter.ISifterRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipeManager;

/**
 * Created by cout970 on 22/08/2016.
 */
public class MagneticraftApi {

    private MagneticraftApi() {
    }

    public static IHydraulicPressRecipeManager getHydraulicPressRecipeManager() {
        return HydraulicPressRecipeManager.INSTANCE;
    }

    public static IGrinderRecipeManager getGrinderRecipeManager() {
        return GrinderRecipeManager.INSTANCE;
    }

    public static IIceboxRecipeManager getIceboxRecipeManager() {
        return IceboxRecipeManager.INSTANCE;
    }

    public static IKilnRecipeManager getKilnRecipeManager() {
        return KilnRecipeManager.INSTANCE;
    }

    public static ISifterRecipeManager getSifterRecipeManager() {
        return SifterRecipeManager.INSTANCE;
    }

    public static ISluiceBoxRecipeManager getSluiceBoxRecipeManager() {
        return SluiceBoxRecipeManager.INSTANCE;
    }

    public static ICrushingTableRecipeManager getCrushingTableRecipeManager() {
        return CrushingTableRecipeManager.INSTANCE;
    }

    public static IOreGenerationRegistry getOreGenerationRegistry(){
        return OreGenerationRegistry.INSTANCE;
    }
}