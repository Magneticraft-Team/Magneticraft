package com.cout970.magneticraft.api.internal.registries.machines.tablesieve

import com.cout970.magneticraft.api.internal.ApiUtils
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 16/06/2016.
 */
class TableSieveRecipe {

    private val input: ItemStack?            //item inserted in the table sieve
    private val primaryOutput: ItemStack?    //item that you always get from processing the input
    private val secondaryOutput: ItemStack?  //extra item that you may get
    val probability: Float          //probability to get the extra item in [0, 1], can't be less than 0 or bigger than 1

    constructor(input: ItemStack, primaryOutput: ItemStack, secondaryOutput: ItemStack, prob: Float) {
        this.input = input.copy()
        this.primaryOutput = primaryOutput.copy()
        this.secondaryOutput = secondaryOutput.copy()
        this.probability = prob
    }

    constructor(input: ItemStack, primaryOutput: ItemStack) {
        this.input = input.copy()
        this.primaryOutput = primaryOutput.copy()
        this.secondaryOutput = null
        this.probability = 0f
    }

    fun matches(stack: ItemStack): Boolean {
        return ApiUtils.equalsIgnoreSize(stack, input)
    }

    fun getInput(): ItemStack {
        return input!!.copy()
    }

    fun getPrimaryOutput(): ItemStack {
        return primaryOutput!!.copy()
    }

    fun getSecondaryOutput(): ItemStack? {
        return secondaryOutput?.copy()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is TableSieveRecipe) {
            return false
        }

        if (java.lang.Float.compare(o.probability, probability) != 0) {
            return false
        }
        if (if (input != null) input != o.input else o.input != null) {
            return false
        }
        if (if (primaryOutput != null) primaryOutput != o.primaryOutput else o.primaryOutput != null) {
            return false
        }
        return if (secondaryOutput != null) secondaryOutput == o.secondaryOutput else o.secondaryOutput == null

    }

    override fun hashCode(): Int {
        var result = if (input != null) input.hashCode() else 0
        result = 31 * result + if (primaryOutput != null) primaryOutput.hashCode() else 0
        result = 31 * result + if (secondaryOutput != null) secondaryOutput.hashCode() else 0
        result = 31 * result + if (probability != +0.0f) java.lang.Float.floatToIntBits(probability) else 0
        return result
    }

    override fun toString(): String {
        return "TableSieveRecipe{" +
                "input=" + input +
                ", primaryOutput=" + primaryOutput +
                ", secondaryOutput=" + secondaryOutput +
                ", probability=" + probability +
                '}'
    }
}
