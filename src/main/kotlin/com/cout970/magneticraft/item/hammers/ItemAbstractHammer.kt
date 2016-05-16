package com.cout970.magneticraft.item.hammers

import com.cout970.magneticraft.item.ItemBase
import net.minecraft.item.ItemStack

abstract class ItemAbstractHammer(val type: String) : ItemBase("${type}_hammer") {
    abstract val damage: Int

    fun onHit(stack: ItemStack) {
        stack.itemDamage++
    }
}