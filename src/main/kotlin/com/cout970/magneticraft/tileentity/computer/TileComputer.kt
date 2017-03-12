package com.cout970.magneticraft.tileentity.computer

import com.cout970.magneticraft.computer.*
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.resource
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 2016/09/30.
 */
@TileRegister("computer")
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
        val nbt = newNbt {
            add("motherboard", motherboard.serializeNBT())
            add("monitor", monitor.serializeNBT())
            add("floppy", floppy.serializeNBT())
            add("inv", inv.serializeNBT())
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        motherboard.deserializeNBT(nbt.getCompoundTag("motherboard"))
        monitor.deserializeNBT(nbt.getCompoundTag("monitor"))
        floppy.deserializeNBT(nbt.getCompoundTag("floppy"))
        inv.deserializeNBT(nbt.getCompoundTag("inv"))
        super.load(nbt)
    }
}