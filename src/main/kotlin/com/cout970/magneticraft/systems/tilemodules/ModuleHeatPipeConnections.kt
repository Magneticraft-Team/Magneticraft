package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.tileentity.canConnect
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.util.EnumFacing

class ModuleHeatPipeConnections(
    val heatModule: ModuleHeat,
    override val name: String = "module_heat_pipe_connections"
) : IModule {

    override lateinit var container: IModuleContainer

    fun canConnect(side: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos + side) ?: return false
        val handler = tile.getOrNull(HEAT_NODE_HANDLER, side.opposite) ?: return false
        if (handler === heatModule) return false
        val heatNodes = handler.nodes.filterIsInstance<IHeatNode>()

        heatNodes.forEach { otherNode ->
            if (canConnect(heatModule, heatModule.heatNodes[0], handler, otherNode, side.opposite)) {
                return true
            }
        }
        return false
    }
}