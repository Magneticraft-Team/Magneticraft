package com.cout970.magneticraft.api.internal.registries.machines.tablesieve

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 16/06/2016.
 */
data class TableSieveRecipe(
        private val input: ItemStack,
        private val primaryOutput: ItemStack,
        private val secondaryOutput: ItemStack,
        private val probability: Float,
        val oreDict: Boolean
) : ITableSieveRecipe {

    override fun getInput(): ItemStack = input.copy()

    override fun getPrimaryOutput(): ItemStack = primaryOutput.copy()

    override fun getSecondaryOutput(): ItemStack = secondaryOutput.copy()

    override fun getProbability(): Float = probability

    override fun matches(input: ItemStack?): Boolean {
        if (ApiUtils.equalsIgnoreSize(input, this.input)) return true
        if (oreDict) {
            val ids = OreDictionary.getOreIDs(this.input)
            return OreDictionary.getOreIDs(input).any { it in ids }
        }
        return false
    }
}
