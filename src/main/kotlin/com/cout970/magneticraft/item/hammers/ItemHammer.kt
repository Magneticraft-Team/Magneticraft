package com.cout970.magneticraft.item.hammers

import com.cout970.magneticraft.item.ItemBase
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack

abstract class ItemHammer(val type: String) : ItemBase("${type}_hammer") {
    abstract val damage: Int

    fun onHit(stack: ItemStack, hitBy: EntityLivingBase) {
        stack.damageItem(1, hitBy)
    }
}