package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.gui.common.core.DATA_ID_SELECTED_OPTION
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound

class ModuleHydraulicPress(
    override val name: String = "module_hydraulic_press"
) : IModule {

    override lateinit var container: IModuleContainer

    var mode: HydraulicPressMode = HydraulicPressMode.LIGHT

    override fun deserializeNBT(nbt: NBTTagCompound) {
        mode = HydraulicPressMode.values()[nbt.getInteger("mode")]
    }

    override fun serializeNBT() = newNbt {
        add("mode", mode.ordinal)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
            IntSyncVariable(DATA_ID_SELECTED_OPTION, { mode.ordinal }, { mode = HydraulicPressMode.values()[it] })
        )
    }
}