package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/07/01.
 */
class GrinderCraftingProcess(
        val invModule: ModuleInventory,
        val inputSlot: Int,
        val outputSlot0: Int,
        val outputSlot1: Int
) : ICraftingProcess {

    private var cacheKey: ItemStack = getInput()
    private var cacheValue: IGrinderRecipe? = null

    private fun getInput() = invModule.inventory.extractItem(inputSlot, 1, true)

    private fun getRecipe(): IGrinderRecipe? {
        val input = getInput()
        if (cacheKey == input) return cacheValue

        val recipe = MagneticraftApi.getGrinderRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val input = invModule.inventory.extractItem(inputSlot, 1, false)
        val recipe = MagneticraftApi.getGrinderRecipeManager().findRecipe(input) ?: return

        invModule.inventory.insertItem(outputSlot0, recipe.primaryOutput, false)
        if (Math.random() < recipe.probability) {
            invModule.inventory.insertItem(outputSlot1, recipe.secondaryOutput, false)
        }
    }

    override fun canCraft(): Boolean {
        val input = invModule.inventory.extractItem(inputSlot, 1, true)
        // check non empty and size >= 1
        if (input.isEmpty) return false

        //check recipe
        val recipe = getRecipe() ?: return false

        //check inventory space
        val insert = invModule.inventory.insertItem(outputSlot0, recipe.primaryOutput, true)
        if (insert.isNotEmpty) return false

        val insert2 = invModule.inventory.insertItem(outputSlot1, recipe.secondaryOutput, true)
        if (insert2.isNotEmpty) return false

        return true
    }

    override fun duration(): Float = getRecipe()?.duration ?: 100f
}