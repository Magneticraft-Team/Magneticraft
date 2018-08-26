package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/07/01.
 */
class HydraulicPressCraftingProcess(
    val inventory: Inventory,
    val inputSlot: Int,
    val outputSlot: Int,
    val mode: () -> HydraulicPressMode
) : ICraftingProcess {

    private var cacheKey: ItemStack = ItemStack.EMPTY
    private var cacheMode: HydraulicPressMode? = null
    private var cacheValue: IHydraulicPressRecipe? = null

    private fun getRecipe(input: ItemStack): IHydraulicPressRecipe? {
        if (ApiUtils.equalsIgnoreSize(cacheKey, input) && mode() == cacheMode) return cacheValue

        val recipe = MagneticraftApi.getHydraulicPressRecipeManager().findRecipe(input, mode())
        cacheKey = input
        cacheValue = recipe
        cacheMode = mode()
        return recipe
    }

    override fun craft() {
        val input = inventory.extractItem(inputSlot, 1, true)
        val recipe = MagneticraftApi.getHydraulicPressRecipeManager().findRecipe(input, mode()) ?: return

        inventory.extractItem(inputSlot, recipe.input.count, false)

        recipe.output.let {
            inventory.insertItem(outputSlot, it, false)
        }
    }

    override fun canCraft(): Boolean {
        val input = inventory.extractItem(inputSlot, 1, true)
        // check non empty and size >= 1
        if (input.isEmpty) return false

        //check recipe
        val recipe = getRecipe(input) ?: return false

        if (recipe.input.count != inventory.extractItem(inputSlot, recipe.input.count, true).count) {
            return false
        }

        //check inventory space
        recipe.output.let {
            val insert = inventory.insertItem(outputSlot, it, true)
            if (insert.isNotEmpty) return false
        }

        return true
    }

    override fun duration(): Float = getRecipe(inventory.extractItem(inputSlot, 1, true))?.duration ?: 100f
}