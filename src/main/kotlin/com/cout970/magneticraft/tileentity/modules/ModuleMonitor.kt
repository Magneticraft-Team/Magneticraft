package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.computer.DeviceMonitor
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.toMap
import com.cout970.magneticraft.util.toNBT
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/07/07.
 */
class ModuleMonitor(
        val ref: ITileRef,
        override val name: String = "module_monitor"
) : IModule {

    override lateinit var container: IModuleContainer
    val monitor: DeviceMonitor = DeviceMonitor(ref)

    override fun update() {
        monitor.update()
    }

    override fun serializeNBT(): NBTTagCompound {
        return monitor.serialize().toNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        monitor.deserialize(nbt.toMap())
    }
}