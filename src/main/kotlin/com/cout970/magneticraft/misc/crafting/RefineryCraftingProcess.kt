package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipe
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fluid.orNull
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE

class RefineryCraftingProcess(
    val inputTank: Tank,
    val outputTank0: Tank,
    val outputTank1: Tank,
    val outputTank2: Tank
) : ICraftingProcess {

    private var cacheKey: FluidStack? = null
    private var cacheValue: IRefineryRecipe? = null

    private fun getRecipe(input: FluidStack): IRefineryRecipe? {
        cacheKey?.let { key ->
            if (key.fluid == input.fluid) return cacheValue
        }

        val recipe = MagneticraftApi.getRefineryRecipeManager().findRecipe(input)
        cacheKey = input
        cacheValue = recipe
        return recipe
    }

    override fun craft(world: World) {
        val input = inputTank.fluid.orNull() ?: return
        val recipe = getRecipe(input) ?: return

        inputTank.drain(recipe.input.amount, EXECUTE)
        recipe.output0?.let { outputTank0.fill(it, EXECUTE) }
        recipe.output1?.let { outputTank1.fill(it, EXECUTE) }
        recipe.output2?.let { outputTank2.fill(it, EXECUTE) }
    }

    override fun canCraft(world: World): Boolean {
        val input = inputTank.fluid.orNull() ?: return false

        //check recipe
        val recipe = getRecipe(input) ?: return false

        if (inputTank.fluidAmount < recipe.input.amount) return false

        recipe.output0?.let { out ->
            if ((outputTank0.capacity - outputTank0.fluidAmount) < out.amount) return false
        }
        recipe.output1?.let { out ->
            if ((outputTank1.capacity - outputTank1.fluidAmount) < out.amount) return false
        }
        recipe.output2?.let { out ->
            if ((outputTank2.capacity - outputTank2.fluidAmount) < out.amount) return false
        }

        return true
    }

    override fun duration(): Float = inputTank.fluid.orNull()?.let { getRecipe(it) }?.duration ?: 10f
}