package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.registry.NODE_PROVIDER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricConnector
import com.cout970.magneticraft.util.contains
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 29/06/2016.
 */
class TileElectricConnector : TileElectricBase() {

    var renderCache = -1
    var mainNode = ElectricConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }))

    override fun getMainNode(): IElectricNode = mainNode

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun updateConnections() {
        super.updateConnections()
        resetRenderCache()
        for (x in -8..8) {
            for (y in -8..8) {
                for (z in -8..8) {
                    if (x == 0 && y == 0 && z == 0) continue
                    val tile = worldObj.getTileEntity(pos.add(x, y, z)) ?: continue
                    val provider = NODE_PROVIDER!!.fromTile(tile, null) ?: continue
                    for (n in provider.nodes) {
                        if (n is IWireConnector) {
                            if (n.connectorsSize == mainNode.connectorsSize) {
                                if (!provider.connections.filter { n in it }.any { mainNode in it }) {
                                    internalConnections.add(ElectricConnection(mainNode, n as IElectricNode))
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
}