package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.registry.NODE_PROVIDER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 03/07/2016.
 */
class TileElectricPole : TileElectricBase() {

    var mainNode = ElectricPoleConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }))
    var renderCache = -1

    override fun getMainNode(): IElectricNode = mainNode

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
                        if (n is IWireConnector && n.connectorsSize == (node as IWireConnector).connectorsSize) {
                            if (tile is TileElectricBase) {
                                if (!tile.connections.any { it.secondNode == node || it.firstNode == node }) {
                                    internalConnections.add(ElectricConnection(node, n as IElectricNode))
                                }
                            } else {
                                internalConnections.add(ElectricConnection(node, n as IElectricNode))
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

    override fun canBeConnected(nodeA: IElectricNode, nodeB: IElectricNode): Boolean = true

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}