package com.cout970.magneticraft.api.internal.registries.machines.gasificationunit

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary

class GasificationUnitRecipe(
    private val input: ItemStack,
    private val itemOutput: ItemStack,
    private val fluidOutput: FluidStack?,
    private val duration: Float,
    private val minTemperature: Float,
    val oreDict: Boolean
) : IGasificationUnitRecipe {

    init {
        require(input.isNotEmpty) { "The recipe input stack cannot be empty!" }
    }

    override fun getInput(): ItemStack = input.copy()

    override fun getItemOutput(): ItemStack = itemOutput.copy()

    override fun getFluidOutput(): FluidStack? = fluidOutput?.copy()

    override fun getDuration(): Float = duration

    override fun minTemperature(): Float = minTemperature

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