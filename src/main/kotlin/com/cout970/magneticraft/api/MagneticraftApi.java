package com.cout970.magneticraft.api;

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager;
import com.cout970.magneticraft.api.internal.registries.machines.tablesieve.TableSieveRecipeManager;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipeManager;
import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipeManager;

/**
 * Created by cout970 on 22/08/2016.
 */
public class MagneticraftApi {

    private MagneticraftApi() {
    }

    public static IHydraulicPressRecipeManager getHydraulicPressRecipeManager() {
        return HydraulicPressRecipeManager.INSTANCE;
    }

    public static ITableSieveRecipeManager getTableSieveRecipeManager() {
        return TableSieveRecipeManager.INSTANCE;
    }

    public static ICrushingTableRecipeManager getCrushingTableRecipeManager() {
        return CrushingTableRecipeManager.INSTANCE;
    }
}