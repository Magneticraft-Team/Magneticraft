package com.cout970.magneticraft.api.registries.machines.tablesieve;

import com.cout970.magneticraft.api.util.ApiUtils;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 16/06/2016.
 */
public final class TableSieveRecipe {

    private final ItemStack input;            //item inserted in the table sieve
    private final ItemStack primaryOutput;    //item that you always get from processing the input
    private final ItemStack secondaryOutput;  //extra item that you may get
    private final float probability;          //probability to get the extra item in [0, 1], can't be less than 0 or bigger than 1

    public TableSieveRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, float prob) {
        this.input = input.copy();
        this.primaryOutput = primaryOutput.copy();
        this.secondaryOutput = secondaryOutput.copy();
        this.probability = prob;
    }

    public TableSieveRecipe(ItemStack input, ItemStack primaryOutput) {
        this.input = input.copy();
        this.primaryOutput = primaryOutput.copy();
        this.secondaryOutput = null;
        this.probability = 0f;
    }

    public boolean matches(ItemStack stack) {
        return ApiUtils.equalsIgnoreSize(stack, input);
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public ItemStack getPrimaryOutput() {
        return primaryOutput.copy();
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput == null ? null :secondaryOutput.copy();
    }

    public float getProbability() {
        return probability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof TableSieveRecipe)) { return false; }

        TableSieveRecipe that = (TableSieveRecipe) o;

        if (Float.compare(that.probability, probability) != 0) { return false; }
        if (input != null ? !input.equals(that.input) : that.input != null) { return false; }
        if (primaryOutput != null ? !primaryOutput.equals(that.primaryOutput) : that.primaryOutput != null) {
            return false;
        }
        return secondaryOutput != null ? secondaryOutput.equals(that.secondaryOutput) : that.secondaryOutput == null;

    }

    @Override
    public int hashCode() {
        int result = input != null ? input.hashCode() : 0;
        result = 31 * result + (primaryOutput != null ? primaryOutput.hashCode() : 0);
        result = 31 * result + (secondaryOutput != null ? secondaryOutput.hashCode() : 0);
        result = 31 * result + (probability != +0.0f ? Float.floatToIntBits(probability) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TableSieveRecipe{" +
                "input=" + input +
                ", primaryOutput=" + primaryOutput +
                ", secondaryOutput=" + secondaryOutput +
                ", probability=" + probability +
                '}';
    }
}
