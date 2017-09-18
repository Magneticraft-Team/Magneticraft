package com.cout970.magneticraft.integration.crafttweaker

import com.cout970.magneticraft.api.MagneticraftApi
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

/**
 * Created by cout970 on 2017/08/11.
 */

@ZenClass("mods.magneticraft.SluiceBox")
@ZenRegister
object SluiceBox {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack, useOreDict: Boolean) {
        addRecipe(input, output, emptyList(), useOreDict)
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack, extp1: Float, exti1: IItemStack, useOreDict: Boolean) {
        addRecipe(input, output, listOf(extp1 to exti1), useOreDict)
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack, useOreDict: Boolean) {
        addRecipe(input, output, listOf(extp1 to exti1, extp2 to exti2), useOreDict)
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack,
                  extp3: Float, exti3: IItemStack, useOreDict: Boolean) {
        addRecipe(input, output, listOf(extp1 to exti1, extp2 to exti2, extp3 to exti3), useOreDict)
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack,
                  extp3: Float, exti3: IItemStack,
                  extp4: Float, exti4: IItemStack, useOreDict: Boolean) {

        addRecipe(input, output, listOf(
                extp1 to exti1,
                extp2 to exti2,
                extp3 to exti3,
                extp4 to exti4), useOreDict)
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack,
                  extp3: Float, exti3: IItemStack,
                  extp4: Float, exti4: IItemStack,
                  extp5: Float, exti5: IItemStack, useOreDict: Boolean) {

        addRecipe(input, output, listOf(
                extp1 to exti1,
                extp2 to exti2,
                extp3 to exti3,
                extp4 to exti4,
                extp5 to exti5), useOreDict)
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack, extras: List<Pair<Number, IItemStack>>, useOreDict: Boolean) {
        CraftTweakerPlugin.delayExecution {
            val a = input.toStack()
            val b = output.toStack()

            if (a.isEmpty) {
                ctLogError("[SluiceBox] Invalid input stack: $input")
                return@delayExecution
            }
            if (b.isEmpty) {
                ctLogError("[SluiceBox] Invalid output stack: $output")
                return@delayExecution
            }
            val ext = extras.map { it.second.toStack() to it.first.toFloat() }

            val man = MagneticraftApi.getSluiceBoxRecipeManager()
            val recipe = man.createRecipe(a, b, ext, useOreDict)

            applyAction("Adding $recipe") {
                man.registerRecipe(recipe)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack) {
        CraftTweakerPlugin.delayExecution {
            val a = input.toStack()

            if (a.isEmpty) {
                ctLogError("[SluiceBox] Invalid input stack: $input")
                return@delayExecution
            }
            val man = MagneticraftApi.getSluiceBoxRecipeManager()

            val recipe = man.findRecipe(a)
            if (recipe != null) {
                applyAction("Removing $recipe") {
                    man.removeRecipe(recipe)
                }
            } else {
                ctLogError("[SluiceBox] Error removing recipe: Unable to find recipe with input = $input")
            }
        }
    }
}