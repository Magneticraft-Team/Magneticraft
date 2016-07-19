package com.cout970.magneticraft.item.hammers

import com.cout970.magneticraft.item.ItemBase
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack

abstract class ItemHammer(type: String) : ItemBase("${type}_hammer") {
    abstract val damage: Int

    init {
        maxStackSize = 1
    }

    fun onHit(stack: ItemStack, hitBy: EntityLivingBase) {
        stack.damageItem(1, hitBy)
    }
}