package com.cout970.magneticraft.api.registries.machines.tablesieve;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 16/06/2016.
 */
public class TableSieveRegistry {

    private static final List<TableSieveRecipe> recipes = new LinkedList<>();

    private TableSieveRegistry() {
    }

    public static TableSieveRecipe findRecipe(ItemStack stack) {
        for (TableSieveRecipe rec : recipes) {
            if (rec.matches(stack)) {
                return rec;
            }
        }
        return null;
    }

    public static boolean registerRecipe(TableSieveRecipe recipe) {
        if (findRecipe(recipe.getInput()) != null) {
            return false;
        }
        recipes.add(recipe);
        return true;
    }

    public static List<TableSieveRecipe> getRecipes(){
        return new ArrayList<>(recipes);
    }
}
