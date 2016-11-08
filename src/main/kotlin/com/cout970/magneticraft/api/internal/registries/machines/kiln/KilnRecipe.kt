package com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 22/08/2016.
 */
data class KilnRecipe(
        private val input: ItemStack,
        private val output: ItemStack,
        private val ticks: Float,
        private val minTemp: Double,
        private val maxTemp: Double,
        private val oreDict: Boolean
) : IKilnRecipe {

    override fun getInput(): ItemStack = input.copy()

    override fun getOutput(): ItemStack = output.copy()

    override fun getDuration(): Float = ticks

    override fun getMaxTemp(): Double = maxTemp

    override fun getMinTemp(): Double = minTemp

    override fun matches(input: ItemStack?): Boolean {
        if (ApiUtils.equalsIgnoreSize(input, this.input)) return true
        if (oreDict) {
            val ids = OreDictionary.getOreIDs(this.input)
            return OreDictionary.getOreIDs(input).any { it in ids }
        }
        return false
    }
}