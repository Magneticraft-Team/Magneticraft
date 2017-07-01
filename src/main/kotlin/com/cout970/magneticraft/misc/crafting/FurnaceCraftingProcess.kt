package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import net.minecraft.item.crafting.FurnaceRecipes

/**
 * Created by cout970 on 2017/07/01.
 */
class FurnaceCraftingProcess(
        val invModule: ModuleInventory,
        val inputSlot: Int,
        val outputSlot: Int
) : ICraftingProcess {

    override fun craft() {
        val input = invModule.inventory.extractItem(inputSlot, 1, false)
        val output = FurnaceRecipes.instance().getSmeltingResult(input).copy()
        invModule.inventory.insertItem(outputSlot, output, false)
    }

    override fun canCraft(): Boolean {
        val input = invModule.inventory.extractItem(inputSlot, 1, true)
        // check non empty and size >= 1
        if(input.isEmpty) return false

        //check recipe
        val output = FurnaceRecipes.instance().getSmeltingResult(input)
        if(output.isEmpty) return false

        //check inventory space
        val insert = invModule.inventory.insertItem(outputSlot, output, true)
        return insert.isEmpty
    }
}