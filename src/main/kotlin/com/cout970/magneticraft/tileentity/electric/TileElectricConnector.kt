package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.states.PROPERTY_FACING
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricConnector
import com.cout970.magneticraft.util.contains
import com.cout970.magneticraft.util.get
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 29/06/2016.
 */
class TileElectricConnector : TileElectricBase() {

    var renderCache = -1
    var mainNode = ElectricConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }), this)

    override fun getMainNode(): IElectricNode = mainNode

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun updateConnections() {
        super.updateConnections()
        resetRenderCache()
//        wiredConnections.clear()
//        for (provider in getHandlersIn(world, pos.subtract(Vec3i(8, 5, 8)), pos.add(Vec3i(8, 5, 8)), this)) {
//            for (n in provider.nodes) {
//                if (n is IWireConnector) {
//                    if (n.connectorsSize == mainNode.connectorsSize) {
//                        if (!provider.connections.filter { n in it }.any { mainNode in it }) {
//                            wiredConnections.add(ElectricConnection(mainNode, n as IElectricNode))
//                        }
//                    }
//                }
//            }
//        }
    }

    private fun resetRenderCache() {
        if (worldObj.isRemote) {
            if (renderCache != -1) {
                GlStateManager.glDeleteLists(renderCache, 1)
            }
            renderCache = -1
        }
    }

    fun getFacing(): EnumFacing {
        val state = world.getBlockState(pos)
        return PROPERTY_FACING[state]
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == getFacing()
    }

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == this) return result
        println(wiredConnections.size)
        for (n in handler.nodes) {
            if (n is IWireConnector) {
                if (n.connectorsSize == mainNode.connectorsSize) {

                    if (!handler.connections.any { mainNode in it } &&
                            !connections.any { n in it }) {

                        wiredConnections.add(ElectricConnection(mainNode, n as IElectricNode))
                        result = true
                    }
                }
            }
        }
        return result
    }
}