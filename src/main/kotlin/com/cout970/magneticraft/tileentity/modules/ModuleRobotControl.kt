package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.computer.DeviceRobotControl
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer

/**
 * Created by cout970 on 2017/08/22.
 */
class ModuleRobotControl(
        val ref: ITileRef,
        val invModule: ModuleInventory,
        override val name: String = "module_network_card"
) : IModule {

    override lateinit var container: IModuleContainer
    val device = DeviceRobotControl(ref)


}