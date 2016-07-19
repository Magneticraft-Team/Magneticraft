package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.states.PROPERTY_FACING
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricConnector
import com.cout970.magneticraft.util.get
import com.google.common.base.Predicate
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 29/06/2016.
 */
class TileElectricConnector : TileElectricBase() {

    var mainNode = ElectricConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }), this)

    override fun getMainNode(): IElectricNode = mainNode

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun updateWiredConnections() {
        if (autoConnectWires) {
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), mainNode, Predicate { it!!.connectorsSize == mainNode.connectorsSize })
        }
        super.updateWiredConnections()
    }

    fun getFacing(): EnumFacing {
        val state = world.getBlockState(pos)
        return PROPERTY_FACING[state]
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == getFacing()
    }

    override fun getMaxWireDistance(): Double = 8.0

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == this || handler !is IElectricNodeHandler) return result
        for (n in handler.nodes) {
            if (n is IWireConnector) {
                if (n.connectorsSize == mainNode.connectorsSize) {
                    val con = handler.createConnection(this, mainNode, n, null)
                    if (con != null) {
                        wiredConnections.add(con)
                        result = true
                        wireRender.reset()
                    }
                }
            }
        }
        return result
    }
}