package com.cout970.magneticraft;

import com.cout970.magneticraft.api.access.RecipeRegister;

/**
 * Created by cout970 on 17/01/2016.
 */
public class ManagerRecipe {

    public static void init() {
        initCrushingTableRecipes();
        initTableSieveRecipes();
    }

    private static void initCrushingTableRecipes() {
        for(ManagerItems.ItemOres ore : ManagerItems.ItemOres.values()){
            RecipeRegister.registerCrushingTableRecipe(ore.getOreBlock(), ore.getChunk());
        }
    }

    private static void initTableSieveRecipes() {
        for(ManagerItems.ItemOres ore : ManagerItems.ItemOres.values()){
            RecipeRegister.registerSieveTableRecipe(ore.getChunk(), ore.getDust().setAmount(2));
        }
    }
}
