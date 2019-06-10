package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.util.EnumFacing


class ModulePneumaticNetwork(
    override val name: String = "module_pneumatic_network"
) : IModule {

    override lateinit var container: IModuleContainer

    fun accesible(side: EnumFacing): Boolean = true
}