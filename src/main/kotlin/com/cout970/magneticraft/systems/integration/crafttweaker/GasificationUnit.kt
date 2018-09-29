package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.GasificationUnit")
@ZenRegister
object GasificationUnit {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, out1: IItemStack, out2: ILiquidStack, ticks: Float, minTemp: Float, useOreDict: Boolean) {

        CraftTweakerPlugin.delayExecution {
            val inStack = input.toStack()
            val outStack1 = out1.toStack()
            val outStack2 = out2.toStack()

            inStack.ifEmpty {
                ctLogError("[GasificationUnit] Invalid input stack: EMPTY")
                return@delayExecution
            }

            if (outStack1.isEmpty && outStack2 == null) {
                ctLogError("[GasificationUnit] Missing output: item: EMPTY, fluid: null, inputItem: $input")
                return@delayExecution
            }

            if (minTemp < 0) {
                ctLogError("[GasificationUnit] Invalid min temperature: $minTemp, must be positive")
                return@delayExecution
            }

            if (ticks < 0) {
                ctLogError("[GasificationUnit] Invalid processing time: $ticks, must be positive")
                return@delayExecution
            }

            val recipe = GasificationUnitRecipeManager.createRecipe(inStack, outStack1, outStack2, ticks, minTemp, useOreDict)

            applyAction("Adding $recipe") {
                GasificationUnitRecipeManager.registerRecipe(recipe)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack) {
        CraftTweakerPlugin.delayExecution {
            val inStack = input.toStack()

            inStack.ifEmpty {
                ctLogError("[GasificationUnit] Invalid input stack: EMPTY")
                return@delayExecution
            }

            val recipe = GasificationUnitRecipeManager.findRecipe(inStack)

            if (recipe == null) {
                ctLogError("[GasificationUnit] Cannot remove recipe: No recipe found for $input")
                return@delayExecution
            }

            applyAction("Removing $recipe") {
                GasificationUnitRecipeManager.removeRecipe(recipe)
            }
        }
    }
}