package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.registries.machines.oilheater.OilHeaterRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.liquid.ILiquidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.OilHeater")
@ZenRegister
object OilHeater {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: ILiquidStack, output: ILiquidStack, duration: Float, minTemperature: Float) {
        CraftTweakerPlugin.delayExecution {

            val inFluid = input.toStack() ?: run {
                ctLogError("[OilHeater] Invalid input value: $input")
                return@delayExecution
            }
            val outFluid = output.toStack() ?: run {
                ctLogError("[OilHeater] Invalid output value: $output")
                return@delayExecution
            }

            if (minTemperature <= 0) {
                ctLogError("[OilHeater] Invalid minTemperature value: $minTemperature")
                return@delayExecution
            }

            if (duration <= 0) {
                ctLogError("[OilHeater] Invalid duration value: $duration")
                return@delayExecution
            }

            val recipe = OilHeaterRecipeManager.createRecipe(inFluid, outFluid, duration, minTemperature)

            applyAction("Adding $recipe") {
                OilHeaterRecipeManager.registerRecipe(recipe)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: ILiquidStack) {
        CraftTweakerPlugin.delayExecution {
            val a = input.toStack()

            if (a == null) {
                ctLogError("[OilHeater] Invalid input stack: $input")
                return@delayExecution
            }
            val man = MagneticraftApi.getOilHeaterRecipeManager()

            val recipe = man.findRecipe(a)
            if (recipe != null) {
                applyAction("Removing $recipe") {
                    man.removeRecipe(recipe)
                }
            } else {
                ctLogError("[OilHeater] Error removing recipe: Unable to find recipe with input = $input")
            }
        }
    }
}