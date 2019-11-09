package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.systems.tilemodules.ModuleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.world.World

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

    private fun getOutput(input: ItemStack, world: World): ItemStack {
        if (ApiUtils.equalsIgnoreSize(cacheKey, input)) return cacheValue

        val inv = Inventory(1)
        inv.setInventorySlotContents(0, input)

        val recipe = world.recipeManager
            .getRecipe(IRecipeType.SMELTING, inv, world)
            .map { it.recipeOutput }
            .orElse(ItemStack.EMPTY)

        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft(world: World) {
        val input = invModule.inventory.extractItem(inputSlot, 1, false)
        val output = getOutput(input, world).copy()
        invModule.inventory.insertItem(outputSlot, output, false)
    }

    override fun canCraft(world: World): Boolean {
        val input = getInput()
        // check non empty and size >= 1
        if (input.isEmpty) return false

        //check recipe
        val output = getOutput(input, world)
        if (output.isEmpty) return false

        //check inventory space
        val insert = invModule.inventory.insertItem(outputSlot, output, true)
        return insert.isEmpty
    }

    override fun duration(): Float = 100f

    override fun minTemperature(): Float = 60.fromCelsiusToKelvin().toFloat()
}