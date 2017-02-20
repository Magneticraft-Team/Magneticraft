package com.cout970.magneticraft.misc.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot

/**
 * Created by cout970 on 2017/02/20.
 */

/**
 * Gets all slots range is a part of player inventory.
 */
fun Container.getPlayerSlotRanges(player: EntityPlayer) : List<IntRange> {
    return this.getSlotRanges { it.inventory is InventoryPlayer && it.inventory == player.inventory }
}

/**
 * Get all slots range that is not a part of player inventory.
 */
fun Container.getNonPlayerSlotRanges() : List<IntRange> {
    return this.getSlotRanges { it.inventory !is InventoryPlayer }
}

/**
 * Get all slots filtering with [predicate]
 */
fun Container.getSlotRanges(predicate: (Slot) -> Boolean): List<IntRange> {
    val ranges = mutableListOf<IntRange>()
    val size = inventorySlots.size

    var start: Int? = null

    inventorySlots.forEachIndexed { i, slot ->
        if(predicate(slot)) {
            if(start == null)
                start = i
        } else if(start != null) {
            ranges += start!!..i
            start = null
        }
    }

    if(start != null)
        ranges += start!!..size-1

    return ranges
}