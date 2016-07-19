package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.contains
import com.cout970.magneticraft.util.misc.RenderCache
import com.cout970.magneticraft.util.misc.UnloadedElectricConnection
import com.cout970.magneticraft.util.shouldTick
import com.cout970.magneticraft.util.with
import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants

/**
 * Created by cout970 on 29/06/2016.
 */
abstract class TileElectricBase : TileBase(), IElectricNodeHandler, ITickable {

    val wiredConnections = mutableListOf<IElectricConnection>()
    val normalConnections = mutableListOf<IElectricConnection>()
    val node: IElectricNode get() = getMainNode()
    val wireRender = RenderCache()
    val unloadedConnections = mutableListOf<UnloadedElectricConnection>()
    var needUpdate = true
    var firstTicks = -1
    var autoConnectWires = false

    abstract fun getMainNode(): IElectricNode

    override fun update() {
        if (needUpdate || shouldTick(40)) {
            needUpdate = false
            updateConnections()
        }

        if (!unloadedConnections.isEmpty()) {
            val iterator = unloadedConnections.iterator()
            while (iterator.hasNext()) {
                val con = iterator.next()
                val result = con.create(world, this)
                if (result != null) {
                    wiredConnections.add(result)
                    iterator.remove()
                } else if (!con.isValid) {
                    iterator.remove()
                }
            }
            if (!world.isRemote) {
                firstTicks = 60
            } else {
                wireRender.reset()
            }
        }

        if (!world.isRemote) {
            //update to sync connections every 20 seconds
            if (shouldTick(400)) {
                sendUpdateToNearPlayers()
            }
            // when the world is loaded the player take some ticks to
            // load so this wait for the player to send an update
            if (firstTicks > 0) {
                firstTicks--
                if (firstTicks % 20 == 0) {
                    sendUpdateToNearPlayers()
                }
            }
        }
        iterate()
    }

    open fun iterate(){
        node.iterate()
        normalConnections.forEach { if (it.firstNode == node) it.iterate() }
        wiredConnections.forEach { if (it.firstNode == node) it.iterate() }
    }

    open fun updateConnections() {
        clearNormalConnections()
        for (dir in EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }) {
            val tile = worldObj.getTileEntity(pos.offset(dir)) ?: continue
            if (canConnectAtSide(dir)) {
                val provider = NODE_HANDLER!!.fromTile(tile, dir.opposite) ?: continue
                if (provider is IElectricNodeHandler) {
                    for (n in provider.nodes.filter { it is IElectricNode }.map { it as IElectricNode }) {
                        val connection = provider.createConnection(this, node, n, dir.opposite)
                        if (connection != null) {
                            normalConnections.add(connection)
                        }
                    }
                }
            }
        }
        updateWiredConnections()
    }

    open fun updateWiredConnections() {
        if (world.isRemote) wireRender.reset()
        //remove invalid nodes in wiredConnections
        val iterator = wiredConnections.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            val otherNode = if (i.firstNode == node) i.secondNode else i.firstNode
            val handler = getHandler(otherNode)

            if (handler == null || !handler.nodes.contains(otherNode)) {
                iterator.remove()
                continue
            }
        }
    }

    fun clearNormalConnections() {
        val iterator = normalConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val node = if (con.firstNode == node) con.secondNode else con.firstNode
            val handler = getHandler(node)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    fun clearWireConnections() {
        val iterator = wiredConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val node = if (con.firstNode == node) con.secondNode else con.firstNode
            val handler = getHandler(node)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    override fun createConnection(other: IElectricNodeHandler, otherNode: IElectricNode, thisNode: IElectricNode, side: EnumFacing?): IElectricConnection? {
        if (other == this || otherNode == thisNode) return null

        if (wiredConnections.any { otherNode in it }) return null

        val connection = ElectricConnection(otherNode, thisNode)
        if (side == null) {
            if (connection.separationDistance > getMaxWireDistance()) {
                return null
            }
            wiredConnections.add(connection)
        } else {
            normalConnections.add(connection)
        }
        if (world.isRemote) wireRender.reset()
        return connection
    }

    override fun removeConnection(connection: IElectricConnection) {
        normalConnections.remove(connection)
        wiredConnections.remove(connection)
        if (world.isRemote) wireRender.reset()
    }

    open fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean = false

    override fun getNodes(): List<INode> = listOf(node)

    override fun getConnections(): List<IElectricConnection> = normalConnections with wiredConnections

    open fun canConnectAtSide(facing: EnumFacing?): Boolean = true

    open fun getNodeHandler(facing: EnumFacing?): INodeHandler? = if (canConnectAtSide(facing)) this else null

    open fun getMaxWireDistance() = 16.0

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T {
        if (capability == NODE_HANDLER) return getNodeHandler(facing) as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == NODE_HANDLER) return getNodeHandler(facing) != null
        return super.hasCapability(capability, facing)
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("ElectricNode")) {
            node.deserializeNBT(compound.getCompoundTag("ElectricNode"))
        }
        if (compound.hasKey("ElectricConnections")) {
            val list = compound.getTagList("ElectricConnections", Constants.NBT.TAG_COMPOUND)
            for (i in 0 until list.tagCount()) {
                unloadedConnections.add(UnloadedElectricConnection.load(list.getCompoundTagAt(i)))
            }
        }
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        compound!!.setTag("ElectricNode", node.serializeNBT())
        val list = NBTTagList()
        for (i in wiredConnections) {
            if (i.firstNode == node) {
                list.appendTag(UnloadedElectricConnection.save(i))
            }
        }
        for (i in unloadedConnections) {
            if (i.isValid) {
                list.appendTag(UnloadedElectricConnection.save(i))
            }
        }
        compound.setTag("ElectricConnections", list)
        return super.writeToNBT(compound)
    }

    override fun onBreak() {
        super.onBreak()
        clearNormalConnections()
        clearWireConnections()
    }

    companion object {

        fun interpolate(v: Double, min: Double, max: Double): Double {
            if (v < min) return 0.0
            if (v > max) return 1.0
            return (v - min) / (max - min)
        }

        fun autoConnectWires(tile: TileElectricBase, world: World, start: BlockPos, end: BlockPos, node: IWireConnector, filter: Predicate<IWireConnector>) {
            val predicate: Predicate<TileEntity> = Predicate { it != tile }
            for (t in getTileEntitiesIn(world, start, end, predicate)) {
                val handler = NODE_HANDLER!!.fromTile(t)
                if (handler is IElectricNodeHandler) {
                    connect(tile, handler, filter)
                }
            }
        }

        fun connect(tile: TileElectricBase, handler: IElectricNodeHandler, filter: Predicate<IWireConnector> = Predicates.alwaysTrue()) : Boolean{
            var result = false
            for (n in handler.nodes.filter { it is IWireConnector }.map { it as IWireConnector }.filter { filter.apply(it) }) {
                val connection = handler.createConnection(tile, tile.node, n, null)
                if (connection != null) {
                    tile.wiredConnections.add(connection)
                    result = true
                }
            }
            return result
        }

        fun getTileEntitiesIn(world: World, start: BlockPos, end: BlockPos, filter: Predicate<TileEntity>): List<TileEntity> {
            val list = mutableListOf<TileEntity>()
            for (x in start.x..end.x step 16) {
                for (z in start.z..end.z step 16) {
                    val chunk = world.getChunkFromChunkCoords(x shr 4, z shr 4)
                    for ((pos, tile) in chunk.tileEntityMap) {
                        if (!tile.isInvalid && pos in (start to end) && filter.apply(tile)) {
                            list.add(tile)
                        }
                    }
                }
            }
            return list
        }

        fun getHandler(node: IElectricNode): IElectricNodeHandler? {
            val tile = node.world.getTileEntity(node.pos)
            if (tile != null) {
                val handler = NODE_HANDLER!!.fromTile(tile)
                if (handler is IElectricNodeHandler) {
                    return handler
                }
            }
            return null
        }

        operator fun Pair<BlockPos, BlockPos>.contains(pos: BlockPos): Boolean {
            return pos.x >= first.x && pos.x <= second.x &&
                    pos.y >= first.y && pos.y <= second.y &&
                    pos.z >= first.z && pos.z <= second.z
        }
    }
}