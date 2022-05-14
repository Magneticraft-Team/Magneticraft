package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@Suppress("UNUSED")
@ZenClass("mods.magneticraft.HydraulicPress")
@ZenRegister
object HydraulicPress {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, output: IItemStack, ticks: Float, mode: Int) {

        CraftTweakerPlugin.delayExecution {
            val inStack = input.items
            val outStack = output.toStack()

            if (inStack != null) {
                if (inStack.isEmpty()) {
                    ctLogError("[HydraulicPress] Invalid input stack: EMPTY")
                    return@delayExecution
                }
            }

            if (mode > 2 || mode < 0) {
                ctLogError("[HydraulicPress] Invalid mode: $mode, bounds [0, 2]")
                return@delayExecution
            }

            if (ticks < 0) {
                ctLogError("[HydraulicPress] Invalid processing time: $ticks, must be positive")
                return@delayExecution
            }

            val pressMode = HydraulicPressMode.values()[mode]

            for (inputItem in inStack) {
                val recipe = HydraulicPressRecipeManager.createRecipe(inputItem.toStack(), outStack, ticks, pressMode, false)

                applyAction("Adding $recipe") {
                    HydraulicPressRecipeManager.registerRecipe(recipe)
                }
            }

        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack, mode: Int) {
        CraftTweakerPlugin.delayExecution {
            val inStack = input.toStack()

            inStack.ifEmpty {
                ctLogError("[HydraulicPress] Invalid input stack: EMPTY")
                return@delayExecution
            }

            if (mode > 2 || mode < 0) {
                ctLogError("[HydraulicPress] Invalid mode: $mode, bounds [0, 2]")
                return@delayExecution
            }

            val pressMode = HydraulicPressMode.values()[mode]
            val recipe = HydraulicPressRecipeManager.findRecipe(inStack, pressMode)

            if (recipe == null) {
                ctLogError("[HydraulicPress] Cannot remove recipe: No recipe found for $input and mode: $mode")
                return@delayExecution
            }

            applyAction("Removing $recipe") {
                HydraulicPressRecipeManager.removeRecipe(recipe)
            }
        }
    }
}