package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.internal.energy.ElectricConnection
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.PROPERTY_FACING
import com.cout970.magneticraft.integration.IntegrationHandler
import com.cout970.magneticraft.integration.tesla.TeslaNodeWrapper
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.tileentity.IManualWireConnect
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.TraitElectricity.Companion.connectHandlers
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.TESLA_CONSUMER
import com.cout970.magneticraft.registry.TESLA_PRODUCER
import com.cout970.magneticraft.registry.TESLA_STORAGE
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricConnector
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toBlockPos
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 29/06/2016.
 */
@TileRegister("electric_connector")
class TileElectricConnector : TileBase(), IManualWireConnect {

    var mainNode = ElectricConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }), this)

    val traitElectricity = TraitElectricity(this, listOf(mainNode),
            onWireChangeImpl = { if (world.isClient) wireRender.reset() },
            onUpdateConnections = this::onUpdateConnections,
            canConnectAtSideImpl = this::canConnectAtSide,
            maxWireDistance = MAX_WIRE_DISTANCE)

    override val traits: List<ITileTrait> = listOf(traitElectricity)

    val wireRender = RenderCache()
    var hasBase = true
    var tickToNextUpdate = 0
    val teslaWrapper: Any? by lazy { if (IntegrationHandler.TESLA) TeslaNodeWrapper(mainNode) else null }

    override fun update() {
        super.update()
        if (worldObj.isClient) {
            if (tickToNextUpdate > 0)
                tickToNextUpdate--

            if (IntegrationHandler.TESLA) {
                val tile = worldObj.getTileEntity((pos + getFacing()).toBlockPos()) ?: return
                val consumer = TESLA_CONSUMER!!.fromTile(tile, getFacing().opposite) ?: return
                val node: TeslaNodeWrapper = teslaWrapper!! as TeslaNodeWrapper
                val accepted = consumer.givePower(Math.min(node.storedPower, 200L), true)
                if (accepted > 0) {
                    node.takePower(consumer.givePower(accepted, false), false)
                }
            }
        }
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    fun onUpdateConnections() {
        val dir = getFacing()
        if (dir.axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
            val tile = worldObj.getTileEntity(pos.offset(dir, 2))
            if (tile is TileElectricConnector) {
                if (traitElectricity.canConnect(mainNode, tile.traitElectricity, tile.mainNode, dir) &&
                    tile.traitElectricity.canConnect(tile.mainNode, traitElectricity, mainNode, dir.opposite)) {

                    val connection = ElectricConnection(mainNode, tile.mainNode)
                    traitElectricity.addConnection(connection, dir, true)
                    tile.traitElectricity.addConnection(connection, dir.opposite, false)
                }
            }
        }
    }

    fun getFacing(): EnumFacing {
        val state = getBlockState()
        if (PROPERTY_FACING.isIn(state)) {
            return state[PROPERTY_FACING]
        }
        return EnumFacing.DOWN
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == getFacing()
    }


    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == traitElectricity || handler !is IElectricNodeHandler) return result
        result = connectHandlers(traitElectricity, handler)
        wireRender.reset()
        return result
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (facing == getFacing() && (capability == TESLA_CONSUMER || capability == TESLA_PRODUCER || capability == TESLA_STORAGE)) {
            return teslaWrapper as T
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (facing == getFacing() && (capability == TESLA_CONSUMER || capability == TESLA_PRODUCER || capability == TESLA_STORAGE)) return true
        return super.hasCapability(capability, facing)
    }

    companion object {
        val MAX_WIRE_DISTANCE = 8.0
    }
}