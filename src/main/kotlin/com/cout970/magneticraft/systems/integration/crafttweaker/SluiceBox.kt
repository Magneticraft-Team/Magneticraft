package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.MagneticraftApi
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

/**
 * Created by cout970 on 2017/08/11.
 */

@Suppress("UNUSED")
@ZenClass("mods.magneticraft.SluiceBox")
@ZenRegister
object SluiceBox {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, extp0: Float, exti0: IItemStack) {
        addRecipe(input, listOf(extp0 to exti0))
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, extp0: Float, exti0: IItemStack, extp1: Float, exti1: IItemStack) {
        addRecipe(input, listOf(extp0 to exti0, extp1 to exti1))
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient,
                  extp0: Float, exti0: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack) {
        addRecipe(input, listOf(extp0 to exti0, extp1 to exti1, extp2 to exti2))
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient,
                  extp0: Float, exti0: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack,
                  extp3: Float, exti3: IItemStack) {
        addRecipe(input, listOf(extp0 to exti0, extp1 to exti1, extp2 to exti2, extp3 to exti3))
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient,
                  extp0: Float, exti0: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack,
                  extp3: Float, exti3: IItemStack,
                  extp4: Float, exti4: IItemStack) {

        addRecipe(input, listOf(
            extp0 to exti0,
            extp1 to exti1,
            extp2 to exti2,
            extp3 to exti3,
            extp4 to exti4))
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient,
                  extp0: Float, exti0: IItemStack,
                  extp1: Float, exti1: IItemStack,
                  extp2: Float, exti2: IItemStack,
                  extp3: Float, exti3: IItemStack,
                  extp4: Float, exti4: IItemStack,
                  extp5: Float, exti5: IItemStack) {

        addRecipe(input, listOf(
            extp0 to exti0,
            extp1 to exti1,
            extp2 to exti2,
            extp3 to exti3,
            extp4 to exti4,
            extp5 to exti5))
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, extras: List<Pair<Number, IItemStack>>) {
        CraftTweakerPlugin.delayExecution {
            val a = input.items

            if (a.isEmpty()) {
                ctLogError("[SluiceBox] Invalid input stack: $input")
                return@delayExecution
            }
            if (extras.isEmpty()) {
                ctLogError("[SluiceBox] Invalid output: Empty")
                return@delayExecution
            }
            val ext = extras.map { it.second.toStack() to it.first.toFloat() }

            val man = MagneticraftApi.getSluiceBoxRecipeManager()

            for (inputItem in a) {
                val recipe = man.createRecipe(inputItem.toStack(), ext, false)

                applyAction("Adding $recipe") {
                    man.registerRecipe(recipe)
                }
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