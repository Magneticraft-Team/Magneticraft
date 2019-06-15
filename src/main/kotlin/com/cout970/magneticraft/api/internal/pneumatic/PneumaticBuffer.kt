package com.cout970.magneticraft.api.internal.pneumatic

import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.misc.forEachTag
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.INBTSerializable
import java.util.*

class PneumaticBuffer : INBTSerializable<NBTTagList> {
    private val queue = ArrayDeque<PneumaticBox>()
    var blocked = false

    fun add(item: ItemStack) {
        if (item.isNotEmpty) {
            queue.add(PneumaticBox(item.copy()))
        }
    }

    fun add(item: PneumaticBox) {
        queue.add(item)
    }

    fun pop(): PneumaticBox {
        if (queue.size <= 1) blocked = false
        return queue.pop()
    }

    fun ejectItems(consumer: (PneumaticBox) -> Boolean) {
        while (queue.isNotEmpty()) {
            if (consumer(queue.first)) {
                pop()
            } else {
                blocked = true
                break
            }
        }
    }

    fun getItems(): Collection<PneumaticBox> = queue

    override fun deserializeNBT(nbt: NBTTagList) {
        queue.clear()
        nbt.forEachTag { queue += PneumaticBox(ItemStack(it)) }
    }

    override fun serializeNBT(): NBTTagList {
        return NBTTagList().also {
            queue.forEach { item -> it.appendTag(item.serializeNBT()) }
        }
    }
}