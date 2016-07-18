package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleAdapterConnector
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import com.cout970.magneticraft.util.contains
import net.minecraft.client.renderer.GlStateManager
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
    var renderCache = -1

    override fun getMainNode(): IElectricNode = mainNode

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun updateConnections() {
        resetRenderCache()
        wiredConnections.clear()
        for (provider in getHandlersIn(world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), this)) {
            for (n in provider.nodes) {
                if (n is IWireConnector) {
                    if (provider.canBeConnected(n, secondNode) && canBeConnected(n, secondNode)) {
                        if (!provider.connections.filter { n in it }.any { secondNode in it }) {
                            wiredConnections.add(ElectricConnection(secondNode, n as IElectricNode))
                        }

                    } else if (provider.canBeConnected(n, firstNode) && canBeConnected(n, firstNode) && (provider !is TileElectricPoleAdapter)) {
                        if (!provider.connections.filter { n in it }.any { firstNode in it }) {
                            wiredConnections.add(ElectricConnection(firstNode, n as IElectricNode))
                        }
                    }
                }
            }
        }
    }

    private fun resetRenderCache() {
        if (worldObj.isRemote) {
            if (renderCache != -1) {
                GlStateManager.glDeleteLists(renderCache, 1)
            }
            renderCache = -1
        }
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean = facing == null

    override fun getNodes(): List<INode> = listOf(firstNode, secondNode)

    override fun canBeConnected(nodeA: IElectricNode, nodeB: IElectricNode): Boolean {
        if (nodeA is IWireConnector && nodeB is IWireConnector) {
            return nodeA.connectorsSize == nodeB.connectorsSize
        } else {
            return false
        }
    }
}