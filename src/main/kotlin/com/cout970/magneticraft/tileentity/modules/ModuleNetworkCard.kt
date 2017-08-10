package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.computer.DeviceNetworkCard
import com.cout970.magneticraft.computer.FakeRef
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer

/**
 * Created by cout970 on 2017/08/10.
 */

class ModuleNetworkCard(
        val ref: ITileRef,
        override val name: String = "module_network_card"
): IModule {

    override lateinit var container: IModuleContainer
    val networkCard = DeviceNetworkCard(FakeRef)

    override fun update() {
        networkCard.update()
    }
}