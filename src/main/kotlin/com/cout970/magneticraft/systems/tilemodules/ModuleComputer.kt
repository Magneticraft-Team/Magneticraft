package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.ICPU
import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRAM
import com.cout970.magneticraft.api.computer.IROM
import com.cout970.magneticraft.misc.toMap
import com.cout970.magneticraft.misc.toNBT
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.computer.*
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import gnu.trove.map.hash.TIntObjectHashMap
import net.minecraft.nbt.NBTTagCompound

class ModuleComputer(
    val cpu: ICPU = CPU_MIPS(),
    val ram: IRAM = RAM(0x20000, true),
    val rom: IROM = ROM("assets/$MOD_ID/cpu/bios.bin"),
    val devices: Map<Int, IDevice>,
    override val name: String = "module_computer"
) : IModule {

    override lateinit var container: IModuleContainer
    val bus: Bus = Bus(ram, TIntObjectHashMap<IDevice>().apply { putAll(devices) })

    val motherboard = Motherboard(cpu, ram, rom, bus)
    lateinit var motherboardDevice: DeviceMotherboard

    override fun init() {
        motherboardDevice = DeviceMotherboard(container.ref, motherboard)
        bus.devices.put(0xFF, motherboardDevice)
    }

    override fun update() {
        if (world.isServer) {
            world.profiler.startSection("Magneticraft Computers")
            motherboard.iterate()
            world.profiler.endSection()
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return motherboard.serialize().toNBT().also {
            it.setTag("motherboardDevice", motherboardDevice.serialize().toNBT())
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        motherboard.deserialize(nbt.toMap())
        motherboardDevice.deserialize(nbt.getCompoundTag("motherboardDevice").toMap())
    }
}
