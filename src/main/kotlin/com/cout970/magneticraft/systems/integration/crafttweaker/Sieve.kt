package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.machines.sieve.SieveRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.Sieve")
@ZenRegister
object Sieve {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack,
                  out1: IItemStack, probOut1: Float,
                  out2: IItemStack, probOut2: Float,
                  out3: IItemStack, probOut3: Float,
                  ticks: Float, useOreDict: Boolean) {

        CraftTweakerPlugin.delayExecution {
            val inStack = input.toStack()
            val outStack1 = out1.toStack()
            val outStack2 = out2.toStack()
            val outStack3 = out3.toStack()

            inStack.ifEmpty {
                ctLogError("[Sieve] Invalid input stack: EMPTY")
                return@delayExecution
            }

            if (probOut1 > 1 || probOut1 < 0) {
                ctLogError("[Sieve] Invalid output probability 1: $probOut1, bounds [1, 0]")
                return@delayExecution
            }
            if (probOut2 > 1 || probOut2 < 0) {
                ctLogError("[Sieve] Invalid output probability 2: $probOut2, bounds [1, 0]")
                return@delayExecution
            }
            if (probOut3 > 1 || probOut3 < 0) {
                ctLogError("[Sieve] Invalid output probability 3: $probOut3, bounds [1, 0]")
                return@delayExecution
            }
            if (ticks < 0) {
                ctLogError("[Sieve] Invalid processing time: $ticks, must be positive")
                return@delayExecution
            }

            val recipe = SieveRecipeManager.createRecipe(inStack,
                outStack1, probOut1,
                outStack2, probOut2,
                outStack3, probOut3,
                ticks, useOreDict)

            applyAction("Adding $recipe") {
                SieveRecipeManager.registerRecipe(recipe)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack) {
        CraftTweakerPlugin.delayExecution {
            val inStack = input.toStack()

            inStack.ifEmpty {
                ctLogError("[Sieve] Invalid input stack: EMPTY")
                return@delayExecution
            }

            val recipe = SieveRecipeManager.findRecipe(inStack)

            if (recipe == null) {
                ctLogError("[Sieve] Cannot remove recipe: No recipe found for $input")
                return@delayExecution
            }

            applyAction("Removing $recipe") {
                SieveRecipeManager.removeRecipe(recipe)
            }
        }
    }
}