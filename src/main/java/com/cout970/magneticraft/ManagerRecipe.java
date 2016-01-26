package com.cout970.magneticraft;

import com.cout970.magneticraft.api.access.RecipeRegister;
import net.darkaqua.blacksmith.api.inventory.IItemStack;

/**
 * Created by cout970 on 17/01/2016.
 */
public class ManagerRecipe {

    public static void init() {
        initCrushingTableRecipes();
        initTableSieveRecipes();
    }

    private static void initCrushingTableRecipes() {
        addCrushingTableRecipe(ManagerBlocks.CopperOre, ManagerItems.ItemOres.Copper);
        addCrushingTableRecipe(ManagerBlocks.TungstenOre, ManagerItems.ItemOres.Tungsten);
    }

    private static void initTableSieveRecipes() {
        RecipeRegister.registerSieveTableRecipe(ManagerItems.ItemOres.Copper.getChunk(), ManagerItems.ItemOres.Copper.getDust());
    }

    private static void addCrushingTableRecipe(ManagerBlocks input, ManagerItems.ItemOres output){
        IItemStack result = output.getChunk();
        RecipeRegister.registerCrushingTableRecipe(input.toItemStack(), result);
    }
}
