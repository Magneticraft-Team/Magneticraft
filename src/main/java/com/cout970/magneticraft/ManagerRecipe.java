package com.cout970.magneticraft;

import com.cout970.magneticraft.api.access.RecipeRegister;
import net.darkaqua.blacksmith.api.inventory.IItemStack;

/**
 * Created by cout970 on 17/01/2016.
 */
public class ManagerRecipe {

    public static void init() {
        initCrusherRecipes();
    }

    private static void initCrusherRecipes() {
        IItemStack result = ManagerItems.ItemOres.Copper.getDust();
        result.setAmount(2);
        RecipeRegister.registerCrushingTableRecipe(ManagerBlocks.CopperOre.toItemStack(), result);
    }

}
