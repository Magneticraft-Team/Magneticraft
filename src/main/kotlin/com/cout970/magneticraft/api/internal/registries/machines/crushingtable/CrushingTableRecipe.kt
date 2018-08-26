package com.cout970.magneticraft.api.internal.registries.machines.crushingtable

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/08/2016.
 */
data class CrushingTableRecipe(
    private val input: ItemStack,
    private val output: ItemStack,
    private val oreDict: Boolean
) : ICrushingTableRecipe {

    init {
        require(input.isNotEmpty) { "Input MUST be a non-empty stack" }
    }

    override fun useOreDictionaryEquivalencies(): Boolean = oreDict

    override fun getInput(): ItemStack = input.copy()

    override fun getOutput(): ItemStack = output.copy()

    override fun matches(input: ItemStack): Boolean {
        if (ApiUtils.equalsIgnoreSize(input, this.input)) return true
        if (oreDict) {
            val ids = OreDictionary.getOreIDs(this.input)
            return OreDictionary.getOreIDs(input).any { it in ids }
        }
        return false
    }
}