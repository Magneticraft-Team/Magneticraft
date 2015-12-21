package com.cout970.magneticraft.api.access;

import net.darkaqua.blacksmith.api.inventory.IItemStack;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 21/12/2015.
 */
public class RecipeRegister {

    public static List<RecipeCrushingTable> crushing_table = new LinkedList<>();

    public static boolean registerCrushingTableRecipe(IItemStack in, IItemStack out) {
        if (in == null || out == null) return false;
        RecipeCrushingTable a = new RecipeCrushingTable(in, out);
        if (!crushing_table.contains(a)) {
            crushing_table.add(a);
            return true;
        }
        return false;
    }
}
