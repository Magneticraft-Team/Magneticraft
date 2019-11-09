package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE

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

    override fun craft(world: World) {
        val recipe = getRecipe(inv.getStackInSlot(input))!!

        inv.extractItem(input, 1, false)
        if(recipe.itemOutput.isNotEmpty){
            inv.insertItem(output, recipe.itemOutput, false)
        }
        recipe.fluidOutput?.let { tank.fill(it, SIMULATE) }
    }

    override fun canCraft(world: World): Boolean {
        val item = inv.extractItem(input, 1, true)
        if (item.isEmpty) return false

        val recipe = getRecipe(item) ?: return false
        if(recipe.itemOutput.isNotEmpty){
            if(inv.insertItem(output, recipe.itemOutput, true)!= ItemStack.EMPTY) return false
        }
        recipe.fluidOutput?.let {
            if (tank.fill(it, SIMULATE) != it.amount) return false
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