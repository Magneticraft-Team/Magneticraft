package com.cout970.magneticraft.api.internal.pneumatic

import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.misc.forEachTag
import net.minecraft.nbt.ListNBT
import net.minecraftforge.common.util.INBTSerializable

class PneumaticBoxStorage : INBTSerializable<ListNBT> {
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

    override fun deserializeNBT(nbt: ListNBT) {
        items.clear()
        nbt.forEachTag { tag ->
            items += PneumaticBox(tag)
        }
    }

    override fun serializeNBT(): ListNBT {
        return ListNBT().also {
            items.forEach { item -> it.add(item.serializeNBT()) }
        }
    }
}