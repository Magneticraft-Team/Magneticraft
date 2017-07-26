package com.cout970.magneticraft.api.internal.registries.machines.sluicebox

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 16/06/2016.
 */
data class SluiceBoxRecipe(
        private val input: ItemStack,
        private val primaryOutput: ItemStack,
        private val secondaryOutput: List<Pair<ItemStack, Float>>,
        val oreDict: Boolean
) : ISluiceBoxRecipe {

    override fun useOreDictionaryEquivalencies(): Boolean = oreDict

    override fun getInput(): ItemStack = input.copy()

    override fun getPrimaryOutput(): ItemStack = primaryOutput.copy()

    override fun getSecondaryOutput(): MutableList<Pair<ItemStack, Float>> {
        return secondaryOutput.map { it.first.copy() to it.second }.toMutableList()
    }

    override fun matches(input: ItemStack): Boolean {
        if (ApiUtils.equalsIgnoreSize(input, this.input)) return true
        if (oreDict) {
            val ids = OreDictionary.getOreIDs(this.input)
            return OreDictionary.getOreIDs(input).any { it in ids }
        }
        return false
    }
}
