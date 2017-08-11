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
class SluiceBox {

    @ZenMethod
    fun addRecipe(input: IItemStack, output: IItemStack, extras: List<List<Any>>, useOreDict: Boolean) {

        val a = input.toStack()
        val b = output.toStack()

        if (a.isEmpty) {
            ctLogError("[SluiceBox] Invalid input stack: $input")
            return
        }
        if (b.isEmpty) {
            ctLogError("[SluiceBox] Invalid output stack: $output")
            return
        }
        val ext = extras.mapNotNull {
            if(it.size != 2){
                ctLogError("[SluiceBox] Invalid extra parameter: Content should be Pairs (Stack, Float), but got: $it")
                return@mapNotNull null
            }
            if(it[0] !is IItemStack){
                ctLogError("[SluiceBox] Invalid extra parameter: Invalid Pair (Stack, Float), but got: $it")
                return@mapNotNull null
            }
            if(it[1] !is Number){
                ctLogError("[SluiceBox] Invalid extra parameter: Invalid Pair (Stack, Float), but got: $it")
                return@mapNotNull null
            }
            Pair((it[0] as IItemStack).toStack(), (it[1] as Number).toFloat())
        }

        val man = MagneticraftApi.getSluiceBoxRecipeManager()
        val recipe = man.createRecipe(a, b, ext, useOreDict)

        applyAction("Adding $recipe") {
            man.registerRecipe(recipe)
        }
    }

    @ZenMethod
    fun removeRecipe(input: IItemStack) {
        val a = input.toStack()

        if (a.isEmpty) {
            ctLogError("[SluiceBox] Invalid input stack: $input")
            return
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