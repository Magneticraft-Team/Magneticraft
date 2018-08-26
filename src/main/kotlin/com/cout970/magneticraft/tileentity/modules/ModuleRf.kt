package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.gui.common.core.DATA_ID_RF
import com.cout970.magneticraft.misc.energy.RfStorage
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
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
            setInteger("${name}_energy", storage.energyStored)
        }
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
        IntSyncVariable(DATA_ID_RF, { storage.energyStored }, { storage.energyStored = it })
    )
}