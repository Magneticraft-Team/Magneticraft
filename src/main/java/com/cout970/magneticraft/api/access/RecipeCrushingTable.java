package com.cout970.magneticraft.api.access;


import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.InventoryUtils;

public class RecipeCrushingTable {

    protected final IItemStack input;
    protected final IItemStack output;

    public RecipeCrushingTable(IItemStack input, IItemStack output) {
        this.input = input;
        this.output = output;
    }

    public IItemStack getInput() {
        return input;
    }

    public IItemStack getOutput() {
        return output;
    }

    public boolean matches(IItemStack i) {
        return InventoryUtils.areEqual(input, i) || InventoryUtils.areOreDictEquivalent(input, i);
    }

    public static RecipeCrushingTable getRecipe(IItemStack i) {
        for (RecipeCrushingTable r : RecipeRegister.crushing_table) {
            if (r.matches(i)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Crushing Table Recipe, Input: " + input.getDisplayName() + ", Output: " + output.getDisplayName();
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