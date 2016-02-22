package com.cout970.magneticraft.api.access;


import net.darkaqua.blacksmith.api.common.inventory.IItemStack;
import net.darkaqua.blacksmith.api.common.inventory.InventoryUtils;

public class RecipeCrushingTable {

    protected final IItemStack input;
    protected final IItemStack output;

    public RecipeCrushingTable(IItemStack input, IItemStack output) {
        this.input = input.copy();
        this.output = output.copy();
    }

    public IItemStack getInput() {
        return input.copy();
    }

    public IItemStack getOutput() {
        return output.copy();
    }

    public boolean matches(IItemStack i) {
        return InventoryUtils.areEqual(input, i) || InventoryUtils.areOreDictEquivalent(input, i);
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