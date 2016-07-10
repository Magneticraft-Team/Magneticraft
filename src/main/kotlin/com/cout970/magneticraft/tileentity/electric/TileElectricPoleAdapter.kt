package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.registry.NODE_PROVIDER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleAdapterConnector
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import com.cout970.magneticraft.util.contains
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound

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
        internalConnections.clear()
        resetRenderCache()
        for (x in -16..16) {
            for (z in -16..16) {
                for (y in -5..5) {
                    if (x == 0 && y == 0 && z == 0) continue
                    val tile = worldObj.getTileEntity(pos.add(x, y, z)) ?: continue
                    val provider = NODE_PROVIDER!!.fromTile(tile, null) ?: continue
                    for (n in provider.nodes) {
                        if (n is IWireConnector) {
                            if (provider.canBeConnected(n, secondNode) && canBeConnected(n, secondNode)) {
                                if (!provider.connections.filter { n in it }.any { secondNode in it }) {
                                    internalConnections.add(ElectricConnection(secondNode, n as IElectricNode))
                                }
                            } else if (provider.canBeConnected(n, firstNode) && canBeConnected(n, firstNode) && (tile !is TileElectricPoleAdapter)) {
                                if (!provider.connections.filter { n in it }.any { firstNode in it }) {
                                    internalConnections.add(ElectricConnection(firstNode, n as IElectricNode))
                                }
                            }
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

    override fun getNodes(): List<INode> = listOf(firstNode, secondNode)

    override fun canBeConnected(nodeA: IElectricNode, nodeB: IElectricNode): Boolean {
        if (nodeA is IWireConnector && nodeB is IWireConnector) {
            return nodeA.connectorsSize == nodeB.connectorsSize
        } else {
            return false
        }
    }
}