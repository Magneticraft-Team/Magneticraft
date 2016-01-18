package com.cout970.magneticraft;

import com.cout970.magneticraft.api.access.RecipeRegister;

/**
 * Created by cout970 on 17/01/2016.
 */
public class ManagerRecipe {

    public static void init(){
        initCrusherRecipes();
    }

    private static void initCrusherRecipes(){
        RecipeRegister.registerCrushingTableRecipe(
                ManagerBlocks.CopperOre.toItemStack(),
                ManagerItems.ItemOres.Copper.getDust());
    }

}
