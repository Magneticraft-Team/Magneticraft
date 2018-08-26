package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.forEach
import com.cout970.magneticraft.util.toMap
import com.cout970.magneticraft.util.toNBT
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/07/07.
 */
class ModuleComputerDevices(
    vararg val parts: IDevice,
    override val name: String = "module_monitor"
) : IModule {

    override lateinit var container: IModuleContainer

    override fun update() {
        for (part in parts) {
            part.update()
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        parts.forEachIndexed { index, dev -> nbt.add(index.toString(), dev.serialize().toNBT()) }
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        nbt.forEach { key, tag ->
            val index = key.toInt()
            parts[index].deserialize((tag as NBTTagCompound).toMap())
        }
    }
}