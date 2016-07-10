package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.vectors.*
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.ELECTRIC_POLE_PLACE
import com.cout970.magneticraft.registry.NODE_PROVIDER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.util.get
import com.google.common.collect.ImmutableList
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by cout970 on 03/07/2016.
 */
class TileElectricPole : TileElectricBase() {

    var renderCache = -1

    override fun createNode(): ElectricNode = ElectricPoleNode(worldGetter = { world }, posGetter = { pos })

    override fun updateConnections() {
        connections.clear()
        resetRenderCache()
        for (x in -16..16) {
            for (z in -16..16) {
                if (x == 0 && z == 0) continue
                val tile = worldObj.getTileEntity(pos.add(x, 0, z)) ?: continue
                val provider = NODE_PROVIDER!!.fromTile(tile, null) ?: continue
                for (n in provider.nodes.filterIsInstance<IWireConnector>()) {
                    if (n.connections.size == (node as ElectricPoleNode).connections.size) {
                        if (tile is TileElectricBase) {
                            if (!tile.connections.any { it.secondNode == node || it.firstNode == node }) {
                                connections.add(ElectricConnection(node, n as IElectricNode))
                            }
                        } else {
                            connections.add(ElectricConnection(node, n as IElectricNode))
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

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    inner class ElectricPoleNode(worldGetter: () -> World, posGetter: () -> BlockPos) : ElectricNode(worldGetter = worldGetter, posGetter = posGetter), IWireConnector {
        override fun getConnections(): ImmutableList<Vec3d> {
            val state = world.getBlockState(pos)
            val offset = ELECTRIC_POLE_PLACE[state].offset
            val first = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).add(offset)
            val center = Vec3d(0.5, 1.0, 0.5)
            val last = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).subtract(offset)
            return ImmutableList.of(first, center, last)
        }

        override fun getConnectionIndex(index: Int, connector: IWireConnector, connection: IElectricConnection): Int {

            if (intersection(connections.first().add(pos.toDoubleVec()), connector.connections.first().add(connector.pos.toDoubleVec()),
                    connections.last().add(pos.toDoubleVec()), connector.connections.last().add(connector.pos.toDoubleVec()))) {
                return 2 - index
            }
            return index
        }

        fun intersection(aFirst: Vec3d, aSecond: Vec3d, bFirst: Vec3d, bSecond: Vec3d): Boolean {
            val da = aSecond - aFirst
            val db = bSecond - bFirst
            val dc = bFirst - aFirst

            if (dc.dotProduct(da.crossProduct(db)) != 0.0) // lines are not coplanar
                return false

            val s = dc.crossProduct(db).dotProduct(da.crossProduct(db)) / norm2(da.crossProduct(db))
            if (s >= 0.0 && s <= 1.0) {
                return true
            }
            return false
        }

        private fun norm2(v: Vec3d): Double = v.x * v.x + v.y * v.y + v.z * v.z
    }
}