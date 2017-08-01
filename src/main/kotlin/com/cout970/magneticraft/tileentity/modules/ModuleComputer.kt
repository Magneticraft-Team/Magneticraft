package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.ICPU
import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IMemory
import com.cout970.magneticraft.api.computer.IROM
import com.cout970.magneticraft.computer.*
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.nbt.NBTTagCompound

class ModuleComputer(
        val cpu: ICPU = CPU_MIPS(),
        val ram: IMemory = RAM(0xFFFF + 1, false),
        val rom: IROM = ROM("assets/$MOD_ID/cpu/bios.bin"),
        val devices: Map<Int, IDevice>,
        override val name: String = "module_computer"
) : IModule {

    override lateinit var container: IModuleContainer
    val bus: Bus = Bus(ram, devices.toMutableMap())

    val motherboard = Motherboard(cpu, ram, rom, bus)

    override fun init() {
        bus.devices.put(0xFF, DeviceMotherboard(container.ref, motherboard))
    }

    override fun update() {
        if (world.isServer) {
            world.profiler.startSection("Magneticraft Computers")
            motherboard.iterate()
            world.profiler.endSection()
        }
    }

    override fun serializeNBT(): NBTTagCompound = motherboard.serializeNBT()

    override fun deserializeNBT(nbt: NBTTagCompound) {
        motherboard.deserializeNBT(nbt)
    }
}