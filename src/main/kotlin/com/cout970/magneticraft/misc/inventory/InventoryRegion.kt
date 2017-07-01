package com.cout970.magneticraft.misc.inventory

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/07/01.
 */
data class InventoryRegion(
        val region: IntRange,
        val inverseDirection: Boolean = false,
        val filter: (ItemStack) -> Boolean = { true }
)