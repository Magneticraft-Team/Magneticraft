package com.cout970.magneticraft.misc.inventory

import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 2017/08/22.
 */

class Inventory(size: Int) : ItemStackHandler(size) {

    var onContentsChanges: ((Inventory, Int) -> Unit)? = null

    override fun onContentsChanged(slot: Int) {
        onContentsChanges?.invoke(this, slot)
    }
}