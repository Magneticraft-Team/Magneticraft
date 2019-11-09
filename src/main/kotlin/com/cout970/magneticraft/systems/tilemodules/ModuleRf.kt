package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.NBTTagCompound
import com.cout970.magneticraft.getInteger
import com.cout970.magneticraft.misc.energy.RfStorage
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.set
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.systems.gui.DATA_ID_RF
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraftforge.common.capabilities.Capability

class ModuleRf(
    val storage: RfStorage,
    override val name: String = "module_rf"
) : IModule {

    override lateinit var container: IModuleContainer

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if (cap == FORGE_ENERGY) {
            return storage as T
        }
        return null
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        storage.energyStored = nbt.getInteger("${name}_energy")
    }

    override fun serializeNBT(): NBTTagCompound {
        return super.serializeNBT().apply {
            set("${name}_energy", storage.energyStored)
        }
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
        IntSyncVariable(DATA_ID_RF, { storage.energyStored }, { storage.energyStored = it })
    )
}