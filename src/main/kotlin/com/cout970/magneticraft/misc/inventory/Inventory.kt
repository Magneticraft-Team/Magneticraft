package com.cout970.magneticraft.misc.inventory

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 2017/08/22.
 */

class Inventory(val size: Int, var onContentsChanges: ((Inventory, Int) -> Unit)? = null) : ItemStackHandler(size) {

    override fun onContentsChanged(slot: Int) {
        onContentsChanges?.invoke(this, slot)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        // Avoid crashes when the size of the inventory changes over time
        if (size != nbt.getInteger("Size")) {
            nbt.setInteger("Size", size)
        }
        super.deserializeNBT(nbt)
    }

    override fun toString(): String {
        return "MagneticraftInventory(size=$size)"
    }
}