package com.cout970.magneticraft.api.registries.machines.crushingtable

import coffee.cypher.mcextlib.util.collections.troveMap
import net.minecraft.item.ItemStack
import java.util.*

object CrushingTableRegistry {
    private val recipes = troveMap<ItemStack, ItemStack>(
        hash = { it.item.hashCode() xor it.stackSize xor it.metadata xor (it.tagCompound?.hashCode() ?: 0) },
        equals = ItemStack::areItemStacksEqual
    )

    fun registerRecipe(input: ItemStack, output: ItemStack) {
        if (input.stackSize != 1) {
            throw IllegalArgumentException("Attempted to register recipe {$input -> $output}, only single item inputs are allowed for Crushing Table")
        }
        if (recipes[input] == null) {
            recipes += input.copy() to output.copy()
        } else {
            throw IllegalStateException(
                "Attempted to register recipe {$input -> $output}, but {$input -> ${recipes[input]}} is already present."
            )
        }
    }

    fun getRecipe(input: ItemStack) = recipes[input.copy().apply { stackSize = 1 }]?.copy()

    fun getRecipes() = HashMap(recipes)
}