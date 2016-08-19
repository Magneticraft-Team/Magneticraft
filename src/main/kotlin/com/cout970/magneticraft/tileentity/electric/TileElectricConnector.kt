package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.vectors.toBlockPos
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.states.PROPERTY_FACING
import com.cout970.magneticraft.integration.IntegrationHandler
import com.cout970.magneticraft.integration.tesla.TeslaNodeWrapper
import com.cout970.magneticraft.registry.TESLA_CONSUMER
import com.cout970.magneticraft.registry.TESLA_PRODUCER
import com.cout970.magneticraft.registry.TESLA_STORAGE
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricConnector
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.isIn
import com.cout970.magneticraft.util.plus
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3i
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 29/06/2016.
 */
class TileElectricConnector : TileElectricBase() {

    var mainNode = ElectricConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }), this)
    override val electricNodes: List<IElectricNode>
        get() = listOf(mainNode)
    var hasBase = true
    var tickToNextUpdate = 0
    val teslaWrapper: Any? by lazy { if (IntegrationHandler.TESLA) TeslaNodeWrapper(mainNode) else null }

    override fun update() {
        super.update()
        if (worldObj.isRemote) {
            if (tickToNextUpdate > 0)
                tickToNextUpdate--

            if (IntegrationHandler.TESLA) {
                val tile = worldObj.getTileEntity((pos + getFacing()).toBlockPos()) ?: return
                val consumer = TESLA_CONSUMER!!.fromTile(tile, getFacing()) ?: return
                val node: TeslaNodeWrapper = teslaWrapper!! as TeslaNodeWrapper
                val accepted = consumer.givePower(Math.min(node.storedPower, 200L), true)
                if (accepted > 0) {
                    node.takePower(consumer.givePower(accepted, false), false)
                }
            }
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun updateWiredConnections() {
        val dir = getFacing()
        if (dir.axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
            val tile = worldObj.getTileEntity(pos.offset(dir, 2))
            if (tile is TileElectricConnector) {
                if (canConnect(mainNode, tile, tile.mainNode, dir) && tile.canConnect(tile.mainNode, this, mainNode, dir.opposite)) {
                    val connection = ElectricConnection(mainNode, tile.mainNode)
                    addConnection(connection, dir, true)
                    tile.addConnection(connection, dir.opposite, false)
                }
            }
        }
        if (autoConnectWires) {
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), mainNode)
        }
        super.updateWiredConnections()
    }

    fun getFacing(): EnumFacing {
        val state = world.getBlockState(pos)
        if (PROPERTY_FACING.isIn(state)) {
            return PROPERTY_FACING[state]
        }
        return EnumFacing.DOWN
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == getFacing()
    }

    override fun getMaxWireDistance(): Double = MAX_WIRE_DISTANCE

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == this || handler !is IElectricNodeHandler) return result
        result = connectHandlers(this, handler)
        wireRender.reset()
        return result
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T {
        if (facing == getFacing() && (capability == TESLA_CONSUMER || capability == TESLA_PRODUCER || capability == TESLA_STORAGE)) {
            return teslaWrapper as T
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (facing == getFacing() && (capability == TESLA_CONSUMER || capability == TESLA_PRODUCER || capability == TESLA_STORAGE)) return true
        return super.hasCapability(capability, facing)
    }

    companion object {
        val MAX_WIRE_DISTANCE = 8.0
    }
}