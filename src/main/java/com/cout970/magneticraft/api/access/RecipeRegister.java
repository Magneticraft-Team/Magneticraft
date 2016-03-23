package com.cout970.magneticraft.api.access;

import net.darkaqua.blacksmith.inventory.InventoryUtils;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cout970 on 21/12/2015.
 */
public class RecipeRegister {

    public static Map<ItemStackWrapper, RecipeCrushingTable> crushing_table = new HashMap<>();
    public static Map<ItemStackWrapper, RecipeTableSieve> sieve_table = new HashMap<>();

    public static boolean registerCrushingTableRecipe(ItemStack in, ItemStack out) {
        if (in == null || out == null) return false;
        RecipeCrushingTable a = new RecipeCrushingTable(in, out);
        ItemStackWrapper wrapper = new ItemStackWrapper(in);
        if (!crushing_table.containsKey(wrapper)) {
            crushing_table.put(wrapper, a);
            return true;
        }
        return false;
    }

    public static boolean registerSieveTableRecipe(ItemStack in, ItemStack out) {
        if (in == null || out == null) return false;
        RecipeTableSieve a = new RecipeTableSieve(in, out);
        ItemStackWrapper wrapper = new ItemStackWrapper(in);
        if (!sieve_table.containsKey(wrapper)) {
            sieve_table.put(wrapper, a);
            return true;
        }
        return false;
    }


    public static RecipeCrushingTable getCrushingTableRecipe(ItemStack i) {
        RecipeCrushingTable r = crushing_table.get(new ItemStackWrapper(i));
        return r != null && r.matches(i) ? r : null;
    }

    public static RecipeTableSieve getTableSieveRecipe(ItemStack i) {
        RecipeTableSieve r = sieve_table.get(new ItemStackWrapper(i));
        return r != null && r.matches(i) ? r : null;
    }

    public static class ItemStackWrapper {

        protected ItemStack stack;

        public ItemStackWrapper(ItemStack stack) {
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
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
            int hash = stack.getItem().hashCode() * 31 + stack.getItemDamage();
            if (stack.getTagCompound() != null) {
                hash = hash * 31 + stack.getTagCompound().hashCode();
            }
            return hash;
        }
    }
}
