package com.cout970.magneticraft.tileentity.computer

import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.computer.*
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.isServer
import com.cout970.magneticraft.util.resource
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 2016/09/30.
 */
class TileComputer : TileBase(), ITickable {

    val motherboard = Motherboard(CPU_MIPS(), RAM(0x10000, false), ROM(resource("bios.bin")), this)
    val inv = ItemStackHandler(1)
    val internetCard = DeviceNetworkCard(this)
    val monitor = DeviceMonitor(this)
    val floppy = DeviceFloppyDrive(this) {
        if (inv[0] == null) null else ITEM_FLOPPY_DISK!!.fromItem(inv[0]!!)
    }

    init {
        motherboard.attach(0, floppy)
        motherboard.attach(1, monitor)
        motherboard.attach(2, internetCard)
    }

    override fun update() {
        if (worldObj.isServer) {
            motherboard.iterate()
            floppy.iterate()
            markDirty()
        }
    }

    override fun save(): NBTTagCompound {
        return NBTTagCompound().apply {
            setTag("motherboard", motherboard.serializeNBT())
            setTag("monitor", monitor.serializeNBT())
            setTag("floppy", floppy.serializeNBT())
            setTag("inv", inv.serializeNBT())
        }
    }

    override fun load(nbt: NBTTagCompound) {
        motherboard.deserializeNBT(nbt.getCompoundTag("motherboard"))
        monitor.deserializeNBT(nbt.getCompoundTag("monitor"))
        floppy.deserializeNBT(nbt.getCompoundTag("floppy"))
        inv.deserializeNBT(nbt.getCompoundTag("inv"))
    }
}