package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/07/01.
 */
class SieveCraftingProcess(
        val invModule: ModuleInventory,
        val inputSlot: Int,
        val outputSlot0: Int,
        val outputSlot1: Int,
        val outputSlot2: Int
) : ICraftingProcess {

    private var cacheKey: ItemStack = getInput()
    private var cacheValue: ISieveRecipe? = null

    private fun getInput() = invModule.inventory.extractItem(inputSlot, 1, true)

    private fun getRecipe(): ISieveRecipe? {
        val input = getInput()
        if (ApiUtils.equalsIgnoreSize(cacheKey, input)) return cacheValue

        val recipe = MagneticraftApi.getSieveRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val input = invModule.inventory.extractItem(inputSlot, 1, false)
        val recipe = MagneticraftApi.getSieveRecipeManager().findRecipe(input) ?: return

        if (Math.random() < recipe.primaryChance) {
            invModule.inventory.insertItem(outputSlot0, recipe.primary, false)
        }

        if (Math.random() < recipe.secondaryChance) {
            invModule.inventory.insertItem(outputSlot1, recipe.secondary, false)
        }

        if (Math.random() < recipe.tertiaryChance) {
            invModule.inventory.insertItem(outputSlot2, recipe.tertiary, false)
        }
    }

    override fun canCraft(): Boolean {
        val input = invModule.inventory.extractItem(inputSlot, 1, true)
        // check non empty and size >= 1
        if (input.isEmpty) return false

        //check recipe
        val recipe = getRecipe() ?: return false

        //check inventory space
        val insert = invModule.inventory.insertItem(outputSlot0, recipe.primary, true)
        if (insert.isNotEmpty) return false

        val insert2 = invModule.inventory.insertItem(outputSlot1, recipe.secondary, true)
        if (insert2.isNotEmpty) return false

        val insert3 = invModule.inventory.insertItem(outputSlot2, recipe.tertiary, true)
        if (insert3.isNotEmpty) return false

        return true
    }

    override fun duration(): Float = getRecipe()?.duration ?: 100f
}