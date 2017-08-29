package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.modules.pipe.INetworkNode
import com.cout970.magneticraft.tileentity.modules.pipe.Network
import com.cout970.magneticraft.tileentity.modules.pipe.PipeNetwork
import com.cout970.magneticraft.tileentity.modules.pipe.PipeType
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/28.
 */
class ModulePipe(
        val type: PipeType,
        override val name: String = "module_pipe"
) : IModule, INetworkNode {

    override lateinit var container: IModuleContainer

    var pipeNetwork: PipeNetwork? = null

    //@formatter:off
    override var network: Network<*>?
        set(it) { pipeNetwork = it as PipeNetwork }
        get() = pipeNetwork
    //@formatter:on

    override val ref: ITileRef get() = container.ref
    override val sides: List<EnumFacing> = EnumFacing.values().toList()

    override fun update() {
        if(world.isServer){
            if (network == null){
                network = PipeNetwork.createNetwork(this).apply { expand() }
            }
        }
    }
}