package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleAdapterConnector
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import com.google.common.base.Predicate
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

    override fun getMainNode(): IElectricNode = mainNode

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun updateWiredConnections() {

        if (autoConnectWires) {
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), firstNode,
                    Predicate { it !is ElectricPoleAdapterConnector && it!!.connectorsSize == firstNode.connectorsSize })
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), secondNode,
                    Predicate { it!!.connectorsSize == secondNode.connectorsSize })
        }
        super.updateWiredConnections()
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean = facing == null

    override fun getNodes(): List<INode> = listOf(firstNode, secondNode)

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == this || handler !is IElectricNodeHandler) return result
        for (n in handler.nodes) {
            if (n is IWireConnector) {
                if (n.connectorsSize == firstNode.connectorsSize) {
                    val con = handler.createConnection(this, firstNode, n, null)
                    if (con != null) {
                        wiredConnections.add(con)
                        result = true
                        wireRender.reset()
                    }
                }else{
                    if (n.connectorsSize == secondNode.connectorsSize) {
                        val con = handler.createConnection(this, secondNode, n, null)
                        if (con != null) {
                            wiredConnections.add(con)
                            result = true
                            wireRender.reset()
                        }
                    }
                }
            }
        }
        return result
    }
}