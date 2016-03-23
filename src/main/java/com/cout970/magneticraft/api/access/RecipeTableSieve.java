package com.cout970.magneticraft.api.access;


import net.darkaqua.blacksmith.inventory.InventoryUtils;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 25/01/2016.
 */
public class RecipeTableSieve {

    protected final ItemStack input;
    protected final ItemStack output;

    public RecipeTableSieve(ItemStack input, ItemStack output) {
        this.input = input.copy();
        this.output = output.copy();
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public boolean matches(ItemStack i) {
        return InventoryUtils.areEqual(input, i) || InventoryUtils.areOreDictEquivalent(input, i);
    }

    @Override
    public String toString() {
        return "Sieve Table Recipe, Input: " + input.getDisplayName() + ", Output: " + output.getDisplayName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeCrushingTable)) return false;

        RecipeCrushingTable that = (RecipeCrushingTable) o;

        if (input != null ? !input.equals(that.input) : that.input != null) return false;
        return !(output != null ? !output.equals(that.output) : that.output != null);
    }

    @Override
    public int hashCode() {
        int result = input != null ? input.hashCode() : 0;
        result = 31 * result + (output != null ? output.hashCode() : 0);
        return result;
    }
}
