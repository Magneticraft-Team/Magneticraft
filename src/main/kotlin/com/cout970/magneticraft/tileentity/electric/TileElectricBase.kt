package com.cout970.magneticraft.tileentity.electric


import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.internal.energy.ElectricConnection
import com.cout970.magneticraft.misc.energy.UnloadedElectricConnection
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.vector.length
import com.cout970.magneticraft.util.vector.minus
import com.cout970.magneticraft.util.with
import com.google.common.base.Predicate
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

    val inputNormalConnections = mutableListOf<IElectricConnection>()
    val outputNormalConnections = mutableListOf<IElectricConnection>()

    val inputWiredConnections = mutableListOf<IElectricConnection>()
    val outputWiredConnections = mutableListOf<IElectricConnection>()

    abstract val electricNodes: List<IElectricNode>

    val unloadedConnections = mutableListOf<UnloadedElectricConnection>()
    val wireRender = RenderCache()
    var firstTicks = -1
    var autoConnectWires = false

    override fun update() {
        if (shouldTick(40)) {
            updateConnections()
        }

        resolveUnloadedConnections()

        if (world.isServer) {
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
            iterate()
        }
    }

    open fun resolveUnloadedConnections() {
        if (!unloadedConnections.isEmpty()) {
            val iterator = unloadedConnections.iterator()
            while (iterator.hasNext()) {
                val con = iterator.next()
                if (con.create(world, this)) {
                    iterator.remove()
                }
            }
            if (!world.isRemote) {
                firstTicks = 60
            } else {
                wireRender.reset()
            }
        }
    }

    open fun iterate() {
        outputNormalConnections.forEach { it.iterate() }
        outputWiredConnections.forEach { it.iterate() }
    }

    open fun updateConnections() {
        clearNormalConnections()
        for (thisNode in nodes.filter { it is IElectricNode }.map { it as IElectricNode }) {
            loop@for (dir in NEGATIVE_DIRECTIONS) {
                val tile = worldObj.getTileEntity(thisNode.pos.offset(dir)) ?: continue@loop
                if (tile === this) continue@loop
                val handler = NODE_HANDLER!!.fromTile(tile, dir.opposite) as? IElectricNodeHandler ?: continue@loop
                for (otherNode in handler.nodes.filter { it is IElectricNode }.map { it as IElectricNode }) {
                    if (this.canConnect(thisNode, handler, otherNode, dir) && handler.canConnect(otherNode, this, thisNode, dir.opposite)) {
                        val connection = ElectricConnection(thisNode, otherNode)
                        this.addConnection(connection, dir, true)
                        handler.addConnection(connection, dir.opposite, false)
                    }
                }
            }
        }
        updateWiredConnections()
    }

    open fun updateWiredConnections() {
        if (world.isRemote) wireRender.reset()
        //remove invalid nodes in wiredConnections
        val iterator = outputWiredConnections.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            val handler = getHandler(i.secondNode)
            if (handler == null || !handler.nodes.contains(i.secondNode)) {
                iterator.remove()
                continue
            }
        }
    }

    fun clearNormalConnections() {
        val iterator = outputNormalConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    fun clearWireConnections() {
        val iterator = outputWiredConnections.iterator()
        while (iterator.hasNext()) {
            val con = iterator.next()
            val handler = getHandler(con.secondNode)
            handler?.removeConnection(con)
            iterator.remove()
        }
    }

    override fun canConnect(thisNode: IElectricNode, other: IElectricNodeHandler, otherNode: IElectricNode, side: EnumFacing?): Boolean {
        if (other == this || otherNode == thisNode) return false
        if (!canConnectAtSide(side)) return false
        if (side == null) {
            if (thisNode !is IWireConnector || otherNode !is IWireConnector) return false
            if (thisNode.connectorsSize != otherNode.connectorsSize) return false

            if (inputWiredConnections.any { it.firstNode == otherNode }) {
                return false
            }
            if (outputWiredConnections.any { it.secondNode == otherNode }) {
                return false
            }
            val distance = (thisNode.pos - otherNode.pos).length
            if (distance > getMaxWireDistance()) {
                return false
            }
        }
        return true
    }

    override fun addConnection(connection: IElectricConnection, side: EnumFacing?, output: Boolean) {
        if (side == null) {
            if (output) {
                outputWiredConnections.add(connection)
            } else {
                inputWiredConnections.add(connection)
            }
            if (world.isRemote) wireRender.reset()
        } else {
            if (output) {
                outputNormalConnections.add(connection)
            } else {
                inputNormalConnections.add(connection)
            }
        }
    }

    override fun removeConnection(connection: IElectricConnection) {
        inputNormalConnections.remove(connection)
        outputNormalConnections.remove(connection)
        inputWiredConnections.remove(connection)
        outputWiredConnections.remove(connection)
        if (world.isRemote) wireRender.reset()
    }

    open fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean = false

    override fun getNodes(): List<INode> = electricNodes

    override fun getInputConnections(): MutableList<IElectricConnection> = inputNormalConnections with inputWiredConnections

    override fun getOutputConnections(): MutableList<IElectricConnection> = outputNormalConnections with outputWiredConnections

    //check for normal connections
    open fun canConnectAtSide(facing: EnumFacing?): Boolean = true

    open fun getNodeHandler(facing: EnumFacing?): INodeHandler? = if (canConnectAtSide(facing)) this else null

    open fun getMaxWireDistance() = 16.0

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == NODE_HANDLER) return getNodeHandler(facing) as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == NODE_HANDLER) return getNodeHandler(facing) != null
        return super.hasCapability(capability, facing)
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("ElectricNodes")) {
            val tag = compound.getCompoundTag("ElectricNodes")
            for (i in 0 until electricNodes.size) {
                electricNodes[i].deserializeNBT(tag.getCompoundTag("Node" + i))
            }
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
        val tag = NBTTagCompound()
        for (i in 0 until electricNodes.size) {
            tag.setTag("Node" + i, electricNodes[i].serializeNBT())
        }
        compound!!.setTag("ElectricNodes", tag)

        val list = NBTTagList()
        for (i in outputWiredConnections) {
            list.appendTag(UnloadedElectricConnection.save(i))
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
        this.let {
            val iterator = inputNormalConnections.iterator()
            while (iterator.hasNext()) {
                val con = iterator.next()
                val handler = getHandler(con.firstNode)
                handler?.removeConnection(con)
                iterator.remove()
            }
        }
        this.let {
            val iterator = inputWiredConnections.iterator()
            while (iterator.hasNext()) {
                val con = iterator.next()
                val handler = getHandler(con.firstNode)
                handler?.removeConnection(con)
                iterator.remove()
            }
        }
    }

    companion object {

        val NEGATIVE_DIRECTIONS = EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }

        fun interpolate(v: Double, min: Double, max: Double): Double {
            if (v < min) return 0.0
            if (v > max) return 1.0
            return (v - min) / (max - min)
        }

        fun autoConnectWires(tile: TileElectricBase, world: World, start: BlockPos, end: BlockPos, node: IWireConnector, filter: (IWireConnector, IWireConnector) -> Boolean = fun(i1, i2) = true) {
            val predicate: Predicate<TileEntity> = Predicate { it != tile }
            for (t in getTileEntitiesIn(world, start, end, predicate)) {
                val handler = NODE_HANDLER!!.fromTile(t)
                if (handler is IElectricNodeHandler) {
                    connectHandlers(tile, handler, filter)
                }
            }
        }

        fun connectHandlers(first: IElectricNodeHandler, second: IElectricNodeHandler, filter: (IWireConnector, IWireConnector) -> Boolean = fun(i1, i2) = true): Boolean {
            var result = false
            for (firstNode in first.nodes.filter { it is IWireConnector }.map { it as IWireConnector }) {
                for (secondNode in second.nodes.filter { it is IWireConnector }.map { it as IWireConnector }) {
                    if (filter.invoke(firstNode, secondNode)) {
                        if (connectNodes(first, firstNode, second, secondNode)) {
                            result = true
                        }
                    }
                }
            }
            return result
        }

        fun connectNodes(first: IElectricNodeHandler, firstNode: IWireConnector, second: IElectricNodeHandler, secondNode: IWireConnector): Boolean {
            if (first.canConnect(firstNode, second, secondNode, null) && second.canConnect(secondNode, first, firstNode, null)) {
                val connection = ElectricConnection(firstNode, secondNode)
                first.addConnection(connection, null, true)
                second.addConnection(connection, null, false)
                return true
            }
            return false
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