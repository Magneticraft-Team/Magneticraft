package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class DeviceInventorySensor(val tile: ITileRef, val inv: Inventory) : IDevice, ITileRef by tile {

    var selectedIndex = 0

    val memStruct = ReadWriteStruct("inventory_sensor_header",
        ReadWriteStruct("device_header",
            ReadOnlyByte("online") { 1 },
            ReadOnlyByte("type") { 5 },
            ReadOnlyShort("status") { 0 }
        ),
        ReadWriteInt("selectedSlot", { selectedIndex = it }, { selectedIndex }),
        ReadOnlyInt("slotCount") { inv.slots },
        ReadWriteStruct("stack",
            ReadOnlyInt("itemId") { Item.getIdFromItem(getStack().item) },
            ReadOnlyInt("itemMeta") { getStack().metadata },
            ReadOnlyInt("itemNbtHash") { getStack().tagCompound?.hashCode() ?: 0 },
            ReadOnlyInt("stackSize") { getStack().count }
        )
    )

    fun getStack(): ItemStack {
        return if (selectedIndex in 0 until inv.slots) inv.getStackInSlot(selectedIndex) else ItemStack.EMPTY
    }

    override fun update() = Unit

    override fun writeByte(bus: IRW, addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    override fun readByte(bus: IRW, addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("index" to selectedIndex)
    }

    override fun deserialize(map: MutableMap<String, Any>) {
        selectedIndex = map["index"] as Int
    }
}