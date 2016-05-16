package com.cout970.magneticraft.api.registries.machines.crushingtable

import net.minecraft.item.ItemStack

private var recipes = emptyMap<ItemStack, ItemStack>()

//Necessary because itemstacks don't have fucking .equals()
private fun getByKey(key: ItemStack) =
    recipes.entries.firstOrNull { ItemStack.areItemStacksEqual(key, it.key) }?.value

fun registerRecipe(input: ItemStack, output: ItemStack) {
    recipes += input to output
}

fun ItemStack.findCrushable() = recipes.keys.firstOrNull { equalsIgnoreSize(it) }?.copy()

fun ItemStack?.isCrushable() = this?.findCrushable()?.let { it.stackSize <= this.stackSize } ?: false

fun ItemStack.crush(): ItemStack {
    val recipe = getByKey(this)

    return recipe?.copy() ?: this
}

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