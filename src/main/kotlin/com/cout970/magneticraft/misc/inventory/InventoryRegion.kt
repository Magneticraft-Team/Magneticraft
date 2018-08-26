package com.cout970.magneticraft.misc.inventory

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/07/01.
 */
data class InventoryRegion(
    val region: IntRange,
    val inverseDirection: Boolean = false,
    val advFilter: (ItemStack, Int) -> Boolean = { _, _ -> true }
) {
    constructor(region: IntRange, inverseDirection: Boolean = false, filter: (ItemStack) -> Boolean)
        : this(region, inverseDirection, { it, _ -> filter(it) })
}