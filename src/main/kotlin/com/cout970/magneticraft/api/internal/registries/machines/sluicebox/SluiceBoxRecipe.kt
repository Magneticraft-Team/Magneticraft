package com.cout970.magneticraft.api.internal.registries.machines.sluicebox

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 16/06/2016.
 */
data class SluiceBoxRecipe(
    private val input: ItemStack,
    private val outputs: List<Pair<ItemStack, Float>>,
    val oreDict: Boolean
) : ISluiceBoxRecipe {

    init {
        require(input.isNotEmpty) { "Input MUST be a non-empty stack" }
    }

    override fun useOreDictionaryEquivalencies(): Boolean = oreDict

    override fun getInput(): ItemStack = input.copy()

    override fun getOutputs(): MutableList<Pair<ItemStack, Float>> {
        return outputs.map { it.first.copy() to it.second }.toMutableList()
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
