package com.cout970.magneticraft.api.access;

import net.darkaqua.blacksmith.api.common.inventory.IItemStack;
import net.darkaqua.blacksmith.api.common.inventory.InventoryUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cout970 on 21/12/2015.
 */
public class RecipeRegister {

    public static Map<ItemStackWrapper, RecipeCrushingTable> crushing_table = new HashMap<>();
    public static Map<ItemStackWrapper, RecipeTableSieve> sieve_table = new HashMap<>();

    public static boolean registerCrushingTableRecipe(IItemStack in, IItemStack out) {
        if (in == null || out == null) return false;
        RecipeCrushingTable a = new RecipeCrushingTable(in, out);
        ItemStackWrapper wrapper = new ItemStackWrapper(in);
        if (!crushing_table.containsKey(wrapper)) {
            crushing_table.put(wrapper, a);
            return true;
        }
        return false;
    }

    public static boolean registerSieveTableRecipe(IItemStack in, IItemStack out) {
        if (in == null || out == null) return false;
        RecipeTableSieve a = new RecipeTableSieve(in, out);
        ItemStackWrapper wrapper = new ItemStackWrapper(in);
        if (!sieve_table.containsKey(wrapper)) {
            sieve_table.put(wrapper, a);
            return true;
        }
        return false;
    }


    public static RecipeCrushingTable getCrushingTableRecipe(IItemStack i) {
        RecipeCrushingTable r = crushing_table.get(new ItemStackWrapper(i));
        return r != null && r.matches(i) ? r : null;
    }

    public static RecipeTableSieve getTableSieveRecipe(IItemStack i) {
        RecipeTableSieve r = sieve_table.get(new ItemStackWrapper(i));
        return r != null && r.matches(i) ? r : null;
    }

    public static class ItemStackWrapper {

        protected IItemStack stack;

        public ItemStackWrapper(IItemStack stack) {
            this.stack = stack;
        }

        public IItemStack getStack() {
            return stack;
        }

        public void setStack(IItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemStackWrapper)) return false;

            ItemStackWrapper that = (ItemStackWrapper) o;

            return InventoryUtils.areEqual(stack, that.stack);
        }

        @Override
        public int hashCode() {
            int hash = stack.getItem().hashCode() * 31 + stack.getDamage();
            if (stack.getDataCompound() != null) {
                hash = hash * 31 + stack.getDataCompound().hashCode();
            }
            return hash;
        }
    }
}
