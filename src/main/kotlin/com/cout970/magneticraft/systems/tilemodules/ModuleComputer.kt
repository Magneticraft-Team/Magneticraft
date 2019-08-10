package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.misc.toMap
import com.cout970.magneticraft.misc.toNBT
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.computer.*
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound


class ModuleComputer(
    val internalDevices: MutableMap<Int, IDevice>,
    override val name: String = "module_computer"
) : IModule {

    override lateinit var container: IModuleContainer

    val motherboard = Motherboard(
        CPU_MIPS(),
        RAM(0x20000, true),
        ROM("assets/$MOD_ID/cpu/bios.bin")
    )
    lateinit var motherboardDevice: DeviceMotherboard

    override fun init() {
        motherboardDevice = DeviceMotherboard(container.ref, motherboard)
        internalDevices[0xFF] = motherboardDevice
        motherboard.deviceMap.putAll(internalDevices)
    }

    override fun update() {
        if (!world.isServer) return
        world.profiler.startSection("Magneticraft Computers")
        motherboard.iterate()
        world.profiler.endSection()

        // TODO
//        if(container.shouldTick(100)){
            // Update devices
//        }
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
