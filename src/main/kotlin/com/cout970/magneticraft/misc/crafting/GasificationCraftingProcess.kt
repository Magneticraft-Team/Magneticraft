package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.systems.integration.crafttweaker.ifNonEmpty
import net.minecraft.item.ItemStack

class GasificationCraftingProcess(
    val tank: Tank,
    val inv: Inventory,
    val input: Int,
    val output: Int
) : IHeatCraftingProcess {

    private var cacheKey: ItemStack = ItemStack.EMPTY
    private var cacheValue: IGasificationUnitRecipe? = null

    private fun getRecipe(input: ItemStack): IGasificationUnitRecipe? {
        if (ApiUtils.equalsIgnoreSize(cacheKey, input)) return cacheValue

        val recipe = MagneticraftApi.getGasificationUnitRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val recipe = getRecipe(inv.getStackInSlot(input))!!

        inv.extractItem(input, 1, false)
        recipe.itemOutput.ifNonEmpty { inv.insertItem(output, it, false) }
        recipe.fluidOutput?.let { tank.fill(it, true) }
    }

    override fun canCraft(): Boolean {
        val item = inv.extractItem(input, 1, true)
        if (item.isEmpty) return false

        val recipe = getRecipe(item) ?: return false
        recipe.itemOutput.ifNonEmpty {
            if (inv.insertItem(output, it, true) != ItemStack.EMPTY) return false
        }
        recipe.fluidOutput?.let {
            if (tank.fill(it, false) != it.amount) return false
        }
        return true
    }

    override fun minTemperature(): Float {
        return getRecipe(inv.getStackInSlot(input))?.minTemperature() ?: STANDARD_AMBIENT_TEMPERATURE.toFloat()
    }

    override fun duration(): Float {
        return getRecipe(inv.getStackInSlot(input))?.duration ?: 10f
    }
}