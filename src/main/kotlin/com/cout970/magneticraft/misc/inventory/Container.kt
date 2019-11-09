package com.cout970.magneticraft.misc.inventory

import com.cout970.magneticraft.EntityPlayer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot

/**
 * Created by cout970 on 2017/02/20.
 */

/**
 * Gets all slots range is a part of player inventory.
 */
fun Container.getPlayerSlotRanges(player: EntityPlayer): List<IntRange> {
    return this.getSlotRanges { it.inventory is PlayerInventory && it.inventory == player.inventory }
}

/**
 * Get all slots range that is not a part of player inventory.
 */
fun Container.getNonPlayerSlotRanges(): List<IntRange> {
    return this.getSlotRanges { it.inventory !is PlayerInventory }
}

/**
 * Get all slots filtering with [predicate]
 */
fun Container.getSlotRanges(predicate: (Slot) -> Boolean): List<IntRange> {
    val ranges = mutableListOf<IntRange>()
    val size = inventorySlots.size

    var start: Int? = null

    inventorySlots.forEachIndexed { i, slot ->
        if (predicate(slot)) {
            if (start == null)
                start = i
        } else if (start != null) {
            ranges += start!!..i
            start = null
        }
    }

    if (start != null)
        ranges += start!! until size

    return ranges
}