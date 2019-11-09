package com.cout970.magneticraft.api.internal

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 16/06/2016.
 */
object ApiUtils {

    fun equalsIgnoreSize(a: ItemStack, b: ItemStack): Boolean {
        return a == b || !(a.isEmpty || b.isEmpty)
            && a.item == b.item
            && (!a.item.isDamageable || a.damage == b.damage)
            && a.tag == b.tag
    }

    fun equalsIgnoreSizeAndNBT(a: ItemStack, b: ItemStack): Boolean {
        return a == b || !(a.isEmpty || b.isEmpty)
            && a.item == b.item
            && (!a.item.isDamageable || a.damage == b.damage)
    }

    fun areEquivalent(a: ItemStack, b: ItemStack): Boolean {
        // TODO add OreDictionary functionality back
        return false
    }
}
