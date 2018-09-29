package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.forEach
import com.cout970.magneticraft.misc.toMap
import com.cout970.magneticraft.misc.toNBT
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
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