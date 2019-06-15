package com.cout970.magneticraft.api.internal.pneumatic

import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.misc.forEachTag
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.INBTSerializable

class PneumaticBoxStorage : INBTSerializable<NBTTagList> {
    private val items = mutableListOf<PneumaticBox>()

    fun insert(box: PneumaticBox) {
        items += box
    }

    fun remove(box: PneumaticBox) {
        items.remove(box)
    }

    fun clear() {
        items.clear()
    }

    fun getItems(): List<PneumaticBox> = items

    override fun deserializeNBT(nbt: NBTTagList) {
        items.clear()
        nbt.forEachTag { tag ->
            items += PneumaticBox(tag)
        }
    }

    override fun serializeNBT(): NBTTagList {
        return NBTTagList().also {
            items.forEach { item -> it.appendTag(item.serializeNBT()) }
        }
    }
}