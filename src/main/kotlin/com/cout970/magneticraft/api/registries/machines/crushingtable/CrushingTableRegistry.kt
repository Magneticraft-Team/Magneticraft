package com.cout970.magneticraft.api.registries.machines.crushingtable

import coffee.cypher.mcextlib.extensions.blocks.stack
import coffee.cypher.mcextlib.extensions.items.stack
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

var recipes = mapOf<ItemStack, ItemStack>(
    Blocks.COBBLESTONE.stack() to Items.CAKE.stack(size = 2)
)
    private set

fun registerRecipe(input: ItemStack, output: ItemStack) {
    recipes += input to output
}

fun ItemStack.findCrushable() = recipes.keys.firstOrNull { equalsIgnoreSize(it) }

fun ItemStack.isCrushable(): Boolean {
    val stack = this

    return stack in recipes
}

fun ItemStack.crush() = recipes[this] ?: this

@Deprecated("Use ExtLib version once it comes out.")
fun ItemStack?.equalsIgnoreSize(that: ItemStack?): Boolean {
    if (this === that) {
        return true
    }

    if (this == null || that == null) {
        return false
    }

    return (item == that.item) && (!item.hasSubtypes || (metadata == that.metadata)) && (tagCompound == that.tagCompound)
}