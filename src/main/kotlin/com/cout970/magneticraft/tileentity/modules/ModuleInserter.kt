package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/06/20.
 */
class ModuleInserter (
        val facingGetter: () -> EnumFacing,
        override val name: String = "module_conveyor_belt"
) : IModule {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()

}