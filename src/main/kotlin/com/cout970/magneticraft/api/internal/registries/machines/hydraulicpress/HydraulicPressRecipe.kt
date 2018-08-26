package com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 22/08/2016.
 */
data class HydraulicPressRecipe(
    private val input: ItemStack,
    private val output: ItemStack,
    private val ticks: Float,
    private val mode: HydraulicPressMode,
    private val oreDict: Boolean
) : IHydraulicPressRecipe {

    override fun getMode(): HydraulicPressMode = mode

    override fun getInput(): ItemStack = input.copy()

    override fun getOutput(): ItemStack = output.copy()

    override fun getDuration(): Float = ticks

    override fun useOreDictionaryEquivalencies(): Boolean = oreDict

    override fun matches(input: ItemStack): Boolean {
        if (input.isEmpty) return false
        if (ApiUtils.equalsIgnoreSize(input, this.input)) return true
        if (oreDict) {
            val ids = OreDictionary.getOreIDs(this.input)
            return OreDictionary.getOreIDs(input).any { it in ids }
        }
        return false
    }
}