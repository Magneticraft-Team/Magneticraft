package com.cout970.magneticraft.api.internal

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 16/06/2016.
 */
object ApiUtils {

    fun equalsIgnoreSize(a: ItemStack, b: ItemStack): Boolean {
        return a == b || !(a.isEmpty || b.isEmpty)
            && a.item == b.item
            && (!a.item.hasSubtypes || a.metadata == b.metadata)
            && a.tagCompound == b.tagCompound
    }

    fun equalsIgnoreSizeAndNBT(a: ItemStack, b: ItemStack): Boolean {
        return a == b || !(a.isEmpty || b.isEmpty)
            && a.item == b.item
            && (!a.item.hasSubtypes || a.metadata == b.metadata)
    }
}
