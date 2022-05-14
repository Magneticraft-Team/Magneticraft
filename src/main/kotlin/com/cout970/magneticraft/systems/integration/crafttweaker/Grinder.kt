package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@Suppress("UNUSED")
@ZenClass("mods.magneticraft.Grinder")
@ZenRegister
object Grinder {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, out1: IItemStack, out2: IItemStack?, probOut2: Float, ticks: Float) {

        CraftTweakerPlugin.delayExecution {
            val inStack: MutableList<IItemStack>? = input.items
            val outStack1 = out1.toStack()
            val outStack2 = out2?.toStack() ?: ItemStack.EMPTY

            if (inStack != null) {
                if (inStack.isEmpty()) {
                ctLogError("[Grinder] Invalid input stack: EMPTY")
                return@delayExecution
                }
            }

            if (probOut2 > 1 || probOut2 < 0) {
                ctLogError("[Grinder] Invalid output probability: $probOut2, bounds [1, 0]")
                return@delayExecution
            }
            if (ticks < 0) {
                ctLogError("[Grinder] Invalid processing time: $ticks, must be positive")
                return@delayExecution
            }

            if (inStack != null) {
                for (inputItem in inStack) {
                    val recipe = GrinderRecipeManager.createRecipe(inputItem.toStack(), outStack1, outStack2, probOut2, ticks, false)

                    applyAction("Adding $recipe") {
                        GrinderRecipeManager.registerRecipe(recipe)
                    }
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
                ctLogError("[Grinder] Invalid input stack: EMPTY")
                return@delayExecution
            }

            val recipe = GrinderRecipeManager.findRecipe(inStack)

            if (recipe == null) {
                ctLogError("[Grinder] Cannot remove recipe: No recipe found for $input")
                return@delayExecution
            }

            applyAction("Removing $recipe") {
                GrinderRecipeManager.removeRecipe(recipe)
            }
        }
    }
}