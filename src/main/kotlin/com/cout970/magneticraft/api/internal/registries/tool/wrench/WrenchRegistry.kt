package com.cout970.magneticraft.api.internal.registries.tool.wrench

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.tool.wrench.IWrenchRegistry
import net.minecraft.item.ItemStack

object WrenchRegistry : IWrenchRegistry {

    private val registry = mutableSetOf<ItemStack>()

    override fun isWrench(stack: ItemStack): Boolean {
        // NBT is used by some mods like the Morph-o-tool to store additional information
        return registry.any { ApiUtils.equalsIgnoreSizeAndNBT(it, stack) }
    }

    override fun registerWrench(stack: ItemStack): Boolean = registry.add(stack.copy())

    override fun removeWrench(stack: ItemStack): Boolean = registry.remove(stack)
}