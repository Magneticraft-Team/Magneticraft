package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.systems.tilemodules.ModuleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes

/**
 * Created by cout970 on 2017/07/01.
 */
class FurnaceCraftingProcess(
    val invModule: ModuleInventory,
    val inputSlot: Int,
    val outputSlot: Int
) : ICraftingProcess, IHeatCraftingProcess {

    private var cacheKey: ItemStack = ItemStack.EMPTY
    private var cacheValue: ItemStack = ItemStack.EMPTY

    private fun getInput() = invModule.inventory.extractItem(inputSlot, 1, true)

    private fun getOutput(input: ItemStack): ItemStack {
        if (ApiUtils.equalsIgnoreSize(cacheKey, input)) return cacheValue

        val recipe = FurnaceRecipes.instance().getSmeltingResult(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft() {
        val input = invModule.inventory.extractItem(inputSlot, 1, false)
        val output = FurnaceRecipes.instance().getSmeltingResult(input).copy()
        invModule.inventory.insertItem(outputSlot, output, false)
    }

    override fun canCraft(): Boolean {
        val input = getInput()
        // check non empty and size >= 1
        if (input.isEmpty) return false

        //check recipe
        val output = getOutput(input)
        if (output.isEmpty) return false

        //check inventory space
        val insert = invModule.inventory.insertItem(outputSlot, output, true)
        return insert.isEmpty
    }

    override fun duration(): Float = 100f

    override fun minTemperature(): Float = 60.fromCelsiusToKelvin().toFloat()
}