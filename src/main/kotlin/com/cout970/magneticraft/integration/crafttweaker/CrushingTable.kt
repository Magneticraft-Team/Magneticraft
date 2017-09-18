package com.cout970.magneticraft.integration.crafttweaker

import com.cout970.magneticraft.api.MagneticraftApi
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod


/**
 * Created by cout970 on 2017/08/11.
 */

@ZenClass("mods.magneticraft.CrushingTable")
@ZenRegister
object CrushingTable {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IItemStack, output: IItemStack, useOreDict: Boolean) {
        CraftTweakerPlugin.delayExecution {
            val a = input.toStack()
            val b = output.toStack()

            if (a.isEmpty) {
                ctLogError("[CrushingTable] Invalid input stack: $input")
                return@delayExecution
            }
            if (b.isEmpty) {
                ctLogError("[CrushingTable] Invalid output stack: $output")
                return@delayExecution
            }
            val man = MagneticraftApi.getCrushingTableRecipeManager()
            val recipe = man.createRecipe(a, b, useOreDict)

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
                ctLogError("[CrushingTable] Invalid input stack: $input")
                return@delayExecution
            }
            val man = MagneticraftApi.getCrushingTableRecipeManager()

            val recipe = man.findRecipe(a)
            if (recipe != null) {
                applyAction("Removing $recipe") {
                    man.removeRecipe(recipe)
                }
            } else {
                ctLogError("[CrushingTable] Error removing recipe: Unable to find recipe with input = $input")
            }
        }
    }
}