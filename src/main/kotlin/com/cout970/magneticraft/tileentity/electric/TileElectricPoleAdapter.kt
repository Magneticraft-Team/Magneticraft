package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleAdapterConnector
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 05/07/2016.
 */
class TileElectricPoleAdapter : TileElectricBase() {

    var mainNode = ElectricNode({ world }, { pos })
    var firstNode = ElectricPoleAdapterConnector(mainNode)
    var secondNode = ElectricPoleConnector(mainNode)
    override val electricNodes: List<IElectricNode>
        get() = listOf(mainNode)

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun updateWiredConnections() {

        if (autoConnectWires) {
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), firstNode)
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), secondNode)
        }
        super.updateWiredConnections()
    }

    override fun canConnect(thisNode: IElectricNode, other: IElectricNodeHandler, otherNode: IElectricNode, side: EnumFacing?): Boolean {
        if(otherNode is ElectricPoleAdapterConnector){
            return false
        }
        return super.canConnect(thisNode, other, otherNode, side)
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean = facing == null

    override fun getNodes(): List<INode> = listOf(secondNode, firstNode)

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        if (handler == this || handler !is IElectricNodeHandler) return false

        return connectHandlers(this, handler, fun(wire1: IWireConnector, wire2: IWireConnector): Boolean {
            return if (wire1 == firstNode) distance(wire1, wire2) <= TileElectricConnector.MAX_WIRE_DISTANCE * TileElectricConnector.MAX_WIRE_DISTANCE else true
        })
    }

    private fun distance(a: IWireConnector, b: IWireConnector): Double {
        return a.pos.distanceSq(b.pos)
    }

    fun loadConnections(connections: List<IElectricConnection>) {
        for (i in connections) {
            val node = if (firstNode == i.firstNode) i.secondNode else i.firstNode
            val handler = getHandler(node)
            if (handler is IElectricNodeHandler) {
                connectHandlers(this, handler)
            }
        }
    }
}