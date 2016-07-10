package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.components.Recipe
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.item.ItemStack

class RecipeBuilder {
    val components = Array(3) {
        Array(3) {
            mutableListOf<ItemStack>()
        }.toList()
    }.toList()

    lateinit var result: ItemStack

    lateinit var position: Vec2d

    fun build() = Recipe(position, components.map { it.toTypedArray() }.toTypedArray(), result)
}