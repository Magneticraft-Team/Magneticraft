package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.registries.machines.refinery.RefineryRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.liquid.ILiquidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.Refinery")
@ZenRegister
object Refinery {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: ILiquidStack, output0: ILiquidStack?, output1: ILiquidStack?, output2: ILiquidStack?, duration: Float) {
        CraftTweakerPlugin.delayExecution {

            val inFluid = input.toStack() ?: run {
                ctLogError("[Refinery] Invalid input value: $input")
                return@delayExecution
            }
            val outFluid0 = output0?.toStack()
            val outFluid1 = output1?.toStack()
            val outFluid2 = output2?.toStack()


            if (duration <= 0) {
                ctLogError("[Refinery] Invalid duration value: $duration")
                return@delayExecution
            }

            if (outFluid0 == null && outFluid1 == null && outFluid2 == null) {
                ctLogError("[Refinery] Error: All outputs null")
                return@delayExecution
            }

            val recipe = RefineryRecipeManager.createRecipe(inFluid, outFluid0, outFluid1, outFluid2, duration)

            applyAction("Adding $recipe") {
                RefineryRecipeManager.registerRecipe(recipe)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: ILiquidStack) {
        CraftTweakerPlugin.delayExecution {
            val a = input.toStack()

            if (a == null) {
                ctLogError("[Refinery] Invalid input stack: $input")
                return@delayExecution
            }
            val man = MagneticraftApi.getRefineryRecipeManager()

            val recipe = man.findRecipe(a)
            if (recipe != null) {
                applyAction("Removing $recipe") {
                    man.removeRecipe(recipe)
                }
            } else {
                ctLogError("[Refinery] Error removing recipe: Unable to find recipe with input = $input")
            }
        }
    }
}