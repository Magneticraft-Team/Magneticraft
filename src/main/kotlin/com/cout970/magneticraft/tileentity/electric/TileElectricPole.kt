package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import com.cout970.magneticraft.util.contains
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 03/07/2016.
 */
class TileElectricPole : TileElectricBase() {

    var mainNode = ElectricPoleConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }))
    var renderCache = -1

    override fun getMainNode(): IElectricNode = mainNode

    override fun updateConnections() {
        resetRenderCache()
        wiredConnections.clear()
        for (provider in getHandlersIn(world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), this)) {
            for (n in provider.nodes) {
                if (n is IWireConnector && n.connectorsSize == (node as IWireConnector).connectorsSize) {
                    if (!provider.connections.filter { n in it }.any { mainNode in it }) {
                        wiredConnections.add(ElectricConnection(node, n as IElectricNode))
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

    override fun canBeConnected(nodeA: IElectricNode, nodeB: IElectricNode): Boolean = true

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}