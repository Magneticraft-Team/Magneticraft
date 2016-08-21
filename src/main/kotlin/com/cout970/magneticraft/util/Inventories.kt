package com.cout970.magneticraft.util

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 07/07/2016.
 */

fun ItemStack.consumeItem(amount: Int = 1): ItemStack? {
    if (stackSize > amount) {
        stackSize -= amount
        return this
    } else {
        return item.getContainerItem(this)
    }
}