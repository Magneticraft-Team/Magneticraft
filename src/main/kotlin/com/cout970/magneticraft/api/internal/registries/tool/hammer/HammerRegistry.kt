package com.cout970.magneticraft.api.internal.registries.tool.hammer

import com.cout970.magneticraft.api.registries.tool.hammer.IHammer
import com.cout970.magneticraft.api.registries.tool.hammer.IHammerRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object HammerRegistry : IHammerRegistry {

    private val registry = mutableMapOf<Item, IHammer>()

    override fun findHammer(stack: ItemStack): IHammer? = registry[stack.item]

    override fun registerHammer(stack: ItemStack, hammer: IHammer): Boolean {
        if (stack.isEmpty || stack.item in registry) return false

        registry += stack.item to hammer
        return true
    }

    override fun removeHammer(stack: ItemStack): Boolean {
        return registry.remove(stack.item) != null
    }

    override fun createHammer(level: Int, speed: Int, cost: Int): IHammer {
        return Hammer(level, speed, cost)
    }
}