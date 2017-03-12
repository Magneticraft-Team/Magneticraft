package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.tileentity.IManualWireConnect
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.TraitElectricity.Companion.connectHandlers
import com.cout970.magneticraft.misc.tileentity.TraitElectricity.Companion.getHandler
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleAdapterConnector
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 05/07/2016.
 */
@TileRegister("electric_pole_adapter")
class TileElectricPoleAdapter : TileBase(), IManualWireConnect {

    var mainNode = ElectricNode({ world }, { pos })
    var firstNode = ElectricPoleAdapterConnector(mainNode)
    var secondNode = ElectricPoleConnector(mainNode)

    val traitElectricity: TraitElectricity = TraitElectricity(this, listOf(firstNode, secondNode),
            onWireChangeImpl = { if (world.isClient) wireRender.reset() },
            canConnectImpl = this::canConnect,
            canConnectAtSideImpl = { it == null })


    override val traits: List<ITileTrait> = listOf(traitElectricity)

    val wireRender = RenderCache()

    fun canConnect(trait: TraitElectricity, thisNode: IElectricNode, other: IElectricNodeHandler,
                   otherNode: IElectricNode, side: EnumFacing?): Boolean {
        if (otherNode is ElectricPoleAdapterConnector) {
            return false
        }
        return trait.defaultCanConnectImpl(thisNode, other, otherNode, side)
    }

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        if (handler == traitElectricity || handler !is IElectricNodeHandler) return false

        return connectHandlers(traitElectricity, handler,
                fun(wire1: IWireConnector, wire2: IWireConnector): Boolean {
                    if (wire1 == firstNode) {
                        return distance(wire1, wire2) <= TileElectricConnector.MAX_WIRE_DISTANCE *
                               TileElectricConnector.MAX_WIRE_DISTANCE
                    } else {
                        return true
                    }
                })
    }

    private fun distance(a: IWireConnector, b: IWireConnector): Double {
        return a.pos.distanceSq(b.pos)
    }

    fun loadConnections(connections: List<IElectricConnection>) {
        connections
                .map { if (firstNode == it.firstNode) it.secondNode else it.firstNode }
                .map(::getHandler)
                .filterIsInstance<IElectricNodeHandler>()
                .forEach { connectHandlers(traitElectricity, it) }
    }
}