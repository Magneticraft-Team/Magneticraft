package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.machines.sieve.SieveRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@Suppress("UNUSED")
@ZenClass("mods.magneticraft.Sieve")
@ZenRegister
object Sieve {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient,
                  out1: IItemStack, probOut1: Float,
                  out2: IItemStack?, probOut2: Float,
                  out3: IItemStack?, probOut3: Float,
                  ticks: Float) {

        CraftTweakerPlugin.delayExecution {
            val inStack = input.items
            val outStack1 = out1.toStack()
            val outStack2 = out2?.toStack() ?: ItemStack.EMPTY
            val outStack3 = out3?.toStack() ?: ItemStack.EMPTY

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

            for (inputItem in inStack) {
                val recipe = SieveRecipeManager.createRecipe(inputItem.toStack(),
                    outStack1, probOut1,
                    outStack2, probOut2,
                    outStack3, probOut3,
                    ticks, false)

                applyAction("Adding $recipe") {
                    SieveRecipeManager.registerRecipe(recipe)
                }
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