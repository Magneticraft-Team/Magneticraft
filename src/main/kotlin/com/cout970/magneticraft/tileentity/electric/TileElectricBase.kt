package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.shouldTick
import com.cout970.magneticraft.util.with
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 29/06/2016.
 */
abstract class TileElectricBase : TileBase(), INodeHandler, ITickable {

    val wiredConnections = mutableListOf<IElectricConnection>()
    val normalConnections = mutableListOf<IElectricConnection>()

    val node: IElectricNode get() = getMainNode()
    protected val internalConnections: List<IElectricConnection> get() = normalConnections with wiredConnections
    var needUpdate = true

    abstract fun getMainNode(): IElectricNode

    override fun update() {
        if (needUpdate || shouldTick(40)) {
            needUpdate = false
            updateConnections()
        }

        node.iterate()
        normalConnections.forEach { it.iterate() }
        wiredConnections.forEach { it.iterate() }
    }

    open fun updateConnections() {
        normalConnections.clear()
        for (dir in EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }) {
            val tile = worldObj.getTileEntity(pos.offset(dir)) ?: continue
            if (canConnectAtSide(dir)) {
                val provider = NODE_HANDLER!!.fromTile(tile, dir.opposite) ?: continue
                for (n in provider.nodes
                        .filter { it is IElectricNode }
                        .map { it as IElectricNode }
                        .filter { provider.canBeConnected(it, node) && canBeConnected(it, node) }) {

                    normalConnections.add(ElectricConnection(node, n))
                }
            }
        }
    }

    open fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean = false

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("ElectricNode")) {
            node.deserializeNBT(compound.getCompoundTag("ElectricNode"))
        }
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        compound!!.setTag("ElectricNode", node.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun getNodes(): List<INode> = listOf(node)

    override fun getConnections(): List<IElectricConnection> = internalConnections

    override fun canBeConnected(nodeA: IElectricNode, nodeB: IElectricNode): Boolean = true

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T {
        if (capability == NODE_HANDLER) return getNodeHandler(facing) as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == NODE_HANDLER) return getNodeHandler(facing) != null
        return super.hasCapability(capability, facing)
    }

    open fun canConnectAtSide(facing: EnumFacing?): Boolean = true

    open fun getNodeHandler(facing: EnumFacing?): INodeHandler? = if (canConnectAtSide(facing)) this else null

    companion object {

        fun interpolate(v: Double, min: Double, max: Double): Double {
            if (v < min) return 0.0
            if (v > max) return 1.0
            return (v - min) / (max - min)
        }

        fun getHandlersIn(world: World, start: BlockPos, end: BlockPos, excluded: TileEntity): List<INodeHandler> {
            val list = mutableListOf<INodeHandler>()
            for (x in start.x..end.x step 16) {
                for (z in start.z..end.z step 16) {
                    val chunk = world.getChunkFromChunkCoords(x shr 4, z shr 4)
                    for ((pos, tile) in chunk.tileEntityMap) {
                        if (!tile.isInvalid && pos in (start to end) && tile != excluded) {
                            val handler = NODE_HANDLER!!.fromTile(tile) ?: continue
                            list.add(handler)
                        }
                    }
                }
            }
            return list
        }

        operator fun Pair<BlockPos, BlockPos>.contains(pos: BlockPos): Boolean {
            return pos.x >= first.x && pos.x <= second.x &&
                    pos.y >= first.y && pos.y <= second.y &&
                    pos.z >= first.z && pos.z <= second.z
        }
    }
}