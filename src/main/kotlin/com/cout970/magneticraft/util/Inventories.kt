package com.cout970.magneticraft.util

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 07/07/2016.
 */

fun consumeItem(stack: ItemStack, amount: Int = 1): ItemStack? {
    if (stack.stackSize > amount) {
        stack.stackSize -= amount
        return stack
    } else {
        return stack.item.getContainerItem(stack)
    }
}