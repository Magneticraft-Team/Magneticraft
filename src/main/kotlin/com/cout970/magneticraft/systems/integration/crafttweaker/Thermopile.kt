package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.Thermopile")
@ZenRegister
object Thermopile {

    @Suppress("DEPRECATION")
    @ZenMethod
    @JvmStatic
    fun addRecipe(block: IItemStack, temperature: Float, conductivity: Float) {
        CraftTweakerPlugin.delayExecution {
            val stack = block.toStack()
            val itemblock = stack.item  as? ItemBlock

            if (itemblock == null) {
                ctLogError("[Thermopile] Invalid input stack: $block, not a block")
                return@delayExecution
            }

            val state = itemblock.block.getStateFromMeta(stack.metadata)

            addRecipe(state, temperature, conductivity)
        }
    }

    @ZenMethod
    @JvmStatic
    fun addRecipe(blockstate: IBlockState, temperature: Float, conductivity: Float) {
        CraftTweakerPlugin.delayExecution {

            if (conductivity <= 0) {
                ctLogError("[Thermopile] Invalid conductivity value: $conductivity")
                return@delayExecution
            }

            val recipe = ThermopileRecipeManager.createRecipe(blockstate, temperature, conductivity)

            applyAction("Adding $recipe") {
                ThermopileRecipeManager.registerRecipe(recipe)
            }
        }
    }

    @Suppress("DEPRECATION")
    @ZenMethod
    @JvmStatic
    fun removeRecipe(block: IItemStack) {
        CraftTweakerPlugin.delayExecution {
            val stack = block.toStack()
            val itemblock = stack.item  as? ItemBlock

            if (itemblock == null) {
                ctLogError("[Thermopile] Invalid input stack: $block, not a block")
                return@delayExecution
            }

            val state = itemblock.block.getStateFromMeta(stack.metadata)

            val recipe = ThermopileRecipeManager.findRecipe(state)

            if (recipe == null) {
                ctLogError("[Thermopile] Cannot remove recipe: No recipe found for $block ($state)")
                return@delayExecution
            }

            applyAction("Removing $recipe") {
                ThermopileRecipeManager.removeRecipe(recipe)
            }
        }
    }
}