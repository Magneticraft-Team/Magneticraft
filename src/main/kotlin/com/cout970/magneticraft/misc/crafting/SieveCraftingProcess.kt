package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.systems.tilemodules.ModuleInventory
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

    private fun getRecipe(input: ItemStack): ISieveRecipe? {
        if (ApiUtils.equalsIgnoreSize(cacheKey, input)) return cacheValue

        val recipe = MagneticraftApi.getSieveRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val input = invModule.inventory.extractItem(inputSlot, 1, false)
        val recipe = MagneticraftApi.getSieveRecipeManager().findRecipe(input) ?: return

        recipe.primary.let {
            if (it.isNotEmpty && Math.random() < recipe.primaryChance) {
                invModule.inventory.insertItem(outputSlot0, it, false)
            }
        }
        recipe.secondary.let {
            if (it.isNotEmpty && Math.random() < recipe.secondaryChance) {
                invModule.inventory.insertItem(outputSlot1, recipe.secondary, false)
            }
        }
        recipe.tertiary.let {
            if (it.isNotEmpty && Math.random() < recipe.tertiaryChance) {
                invModule.inventory.insertItem(outputSlot2, it, false)
            }
        }
    }

    override fun canCraft(): Boolean {
        val input = getInput()
        // check non empty and size >= 1
        if (input.isEmpty) return false

        //check recipe
        val recipe = getRecipe(input) ?: return false

        //check inventory space
        recipe.primary.let {
            if (it.isNotEmpty) {
                val insert = invModule.inventory.insertItem(outputSlot0, it, true)
                if (insert.isNotEmpty) return false
            }
        }
        recipe.secondary.let {
            if (it.isNotEmpty) {
                val insert2 = invModule.inventory.insertItem(outputSlot1, it, true)
                if (insert2.isNotEmpty) return false
            }
        }
        recipe.tertiary.let {
            if (it.isNotEmpty) {
                val insert3 = invModule.inventory.insertItem(outputSlot2, it, true)
                if (insert3.isNotEmpty) return false
            }
        }

        return true
    }

    override fun duration(): Float = getRecipe(getInput())?.duration ?: 100f
}