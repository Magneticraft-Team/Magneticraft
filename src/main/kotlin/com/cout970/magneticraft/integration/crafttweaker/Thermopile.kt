package com.cout970.magneticraft.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager
import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeNoDecay
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemBlock
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.Thermopile")
@ZenRegister
object Thermopile {

    @ZenMethod
    @JvmStatic
    fun addRecipe(block: IItemStack, heat: Int) {
        CraftTweakerPlugin.delayExecution {
            val stack = block.toStack()
            val itemblock = stack.item  as? ItemBlock

            if(itemblock == null) {
                ctLogError("[Thermopile] Invalid input stack: $block, not a block")
                return@delayExecution
            }

            if(heat == 0){
                ctLogError("[Thermopile] Invalid heat value: $heat")
                return@delayExecution
            }

            val recipe = ThermopileRecipeNoDecay(itemblock.block, heat)
            applyAction("Adding $recipe") {
                ThermopileRecipeManager.registerRecipe(recipe)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(block: IItemStack){
        CraftTweakerPlugin.delayExecution {
            val stack = block.toStack()
            val itemblock = stack.item  as? ItemBlock

            if(itemblock == null) {
                ctLogError("[Thermopile] Invalid input stack: $block, not a block")
                return@delayExecution
            }
            val recipe = ThermopileRecipeManager.findRecipe(itemblock.block)

            if(recipe == null) {
                ctLogError("[Thermopile] Cannot remove recipe: No recipe found for $block")
                return@delayExecution
            }

            applyAction("Removing $recipe") {
                ThermopileRecipeManager.removeRecipe(recipe)
            }
        }
    }
}