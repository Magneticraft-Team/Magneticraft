package com.cout970.magneticraft.features.electric_conductors

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.energy.WireConnectorWrapper
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.interpolate
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.tileentity.canConnect
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModuleElectricity
import com.cout970.magneticraft.systems.tilemodules.ModuleTeslaTower
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.Direction
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("connector")
class TileConnector(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = ElectricNode(ref, capacity = 0.25)
    val wrapper = WireConnectorWrapper(node, this::getConnectors, "connector")

    val electricModule = ModuleElectricity(
            electricNodes = listOf(wrapper),
            onWireChange = { if (theWorld.isClient) wireRender.reset() },
            maxWireDistance = 10.0,
            canConnectAtSide = this::canConnectAtSide,
            connectableDirections = this::getConnectableDirections
    )

    var ignoreBlockStateUpdate = false

    val facing: EnumFacing
        get() {
            ignoreBlockStateUpdate = true
            val facing = getBlockState().getFacing()
            ignoreBlockStateUpdate = false
            return facing
        }

    // client
    val wireRender = RenderCache()
    var hasBase: Boolean = false

    init {
        initModules(electricModule)
    }

        override fun tick() {
        super.update()
        if (theWorld.isClient) return

        exportConnectorRF(theWorld, pos, facing, node)

        if (container.shouldTick(10) && theWorld.getClosestPlayer(pos.xd, pos.yd, pos.zd, 8.0, false) != null) {
            sendUpdateToNearPlayers()
        }
    }

    override fun onBlockStateUpdates() {
        if (theWorld.isClient && !ignoreBlockStateUpdate) {
            hasBase = shouldHaveBase(this)
        }
    }

    fun shouldHaveBase(te: TileConnector): Boolean {
        val tile = te.theWorld.getTileEntity(te.pos.offset(te.facing.opposite))
        if (tile != null) {
            val handler = ELECTRIC_NODE_HANDLER!!.fromTile(tile, te.facing)
            if (handler is IElectricNodeHandler) {
                val node = handler.nodes.firstOrNull { it is IElectricNode }
                if (node != null && handler.canConnect(node as IElectricNode, te.electricModule, te.wrapper,
                                te.facing)) {
                    return false
                }
            }
        }
        return true
    }

    fun getConnectableDirections(): List<Pair<BlockPos, EnumFacing>> {
        return if (facing.opposite.axisDirection == Direction.AxisDirection.NEGATIVE) {
            listOf(
                    facing.opposite.toBlockPos() to facing,
                    facing.opposite.toBlockPos() * 2 to facing)
        } else emptyList()
    }

    fun getConnectors(): List<IVector3> {
        val offset = facing.opposite.directionVec.toVec3d() * PIXEL * 3.0
        return listOf(vec3Of(0.5) + offset)
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == this.facing.opposite
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB
}

@RegisterTileEntity("electric_pole")
class TileElectricPole(type: TileType) : TileBase(type), ITickableTileEntity {
    val node = ElectricNode(ref, capacity = 0.25)
    val wrapper = WireConnectorWrapper(node, this::getConnectors, "inter_pole_connector")

    val electricModule = ModuleElectricity(
            electricNodes = listOf(wrapper),
            onWireChange = { if (theWorld.isClient) wireRender.reset() },
            canConnectAtSide = { it == null }
    )

    // client
    val wireRender = RenderCache()

    init {
        electricModule.autoConnectWires = true
        initModules(electricModule)
    }

        override fun tick() {
        super.update()
    }

    fun getConnectors(): List<IVector3> {
        val offset = getBlockState()[Blocks.PROPERTY_POLE_ORIENTATION]?.offset ?: return emptyList()
        val first = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).add(offset)
        val center = Vec3d(0.5, 1.0, 0.5)
        val last = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).subtract(offset)
        return listOf(first, center, last)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB
}

@RegisterTileEntity("electric_pole_transformer")
class TileElectricPoleTransformer(type: TileType) : TileBase(type), ITickableTileEntity {
    val node = ElectricNode(ref, capacity = 0.5)
    val wrapper = WireConnectorWrapper(node, this::getConnectors, "inter_pole_connector")
    val wrapper2 = WireConnectorWrapper(node, this::getConnectors2, "transformer_connector")

    val electricModule = ModuleElectricity(
            electricNodes = listOf(wrapper, wrapper2),
            onWireChange = { if (theWorld.isClient) wireRender.reset() },
            canConnectAtSide = { it == null }
    )

    // client
    val wireRender = RenderCache()

    init {
        electricModule.autoConnectWires = true
        initModules(electricModule)
    }

        override fun tick() {
        super.update()
    }

    fun getConnectors(): List<IVector3> {
        val offset = getBlockState()[Blocks.PROPERTY_POLE_ORIENTATION]?.offset ?: return emptyList()
        val first = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).add(offset)
        val center = Vec3d(0.5, 1.0, 0.5)
        val last = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).subtract(offset)
        return listOf(first, center, last)
    }

    fun getConnectors2(): List<IVector3> {
        val angle = getBlockState()[Blocks.PROPERTY_POLE_ORIENTATION]?.offset ?: return emptyList()
        val offset = angle.rotateYaw(Math.toRadians(-90.0).toFloat())
        val vec = Vec3d(0.5, 1.0 - PIXEL * 6.5, 0.5).add(offset * 0.5)
        return listOf(vec)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB
}

@RegisterTileEntity("electric_cable")
class TileElectricCable(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = ElectricNode(ref, capacity = 0.25)
    val electricModule = ModuleElectricity(listOf(node))

    init {
        initModules(electricModule)
    }

        override fun tick() {
        super.update()
        if (Debug.DEBUG) {
            sendUpdateToNearPlayers()
        }
    }

    fun canConnect(side: EnumFacing): Boolean {
        val tile = theWorld.getTileEntity(pos + side) ?: return false
        val handler = tile.getOrNull(ELECTRIC_NODE_HANDLER, side.opposite)
        if (handler === null || handler === electricModule) return false

        val electricNodes = handler.nodes.filterIsInstance<IElectricNode>()

        electricNodes.forEach { otherNode ->
            if (canConnect(electricModule, node, handler, otherNode, side)) {
                return true
            }
        }
        return false
    }
}


@RegisterTileEntity("tesla_tower")
class TileTeslaTower(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = ElectricNode(ref)
    val electricModule = ModuleElectricity(
            listOf(node),
            capabilityFilter = { side -> side == EnumFacing.DOWN || side == EnumFacing.UP }
    )

    val teslaTowerModule = ModuleTeslaTower(node)

    init {
        initModules(electricModule, teslaTowerModule)
    }

        override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("tesla_tower_part")
class TileTeslaTowerPart(type: TileType) : TileBase(type) {

    // TODO
//    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
//        val other: TileEntity? = when (facing) {
//            EnumFacing.UP -> theWorld.getTileEntity(pos.down())
//            EnumFacing.DOWN -> theWorld.getTileEntity(pos.up())
//            else -> null
//        }
//
//        return other?.getCapability(capability, facing) ?: super.getCapability(capability, facing)
//    }
}

@RegisterTileEntity("energy_receiver")
class TileEnergyReceiver(type: TileType) : TileBase(type), ITickableTileEntity {
    val facing get() = getBlockState().getFacing()
    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
            listOf(node),
            capabilityFilter = { side -> side == null || side == facing.opposite }
    )

    init {
        initModules(electricModule)
    }

        override fun tick() {
        super.update()
        if (theWorld.isClient) return
        exportConnectorRF(theWorld, pos, facing, node)
    }
}

private fun exportConnectorRF(world: World, pos: BlockPos, facing: EnumFacing, node: ElectricNode) {
    if (node.voltage < ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) return

    val tile = world.getTileEntity(pos.offset(facing.opposite))
    val handler = tile?.getOrNull(FORGE_ENERGY, facing) ?: return

    val amount = (interpolate(node.voltage, ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE, ElectricConstants.TIER_1_MAX_VOLTAGE) * 400).toInt()
    val accepted = handler.receiveEnergy(amount, true)

    if (accepted > 0) {
        val received = handler.receiveEnergy(accepted, false)
        node.applyPower(-received.toDouble() * Config.wattsToFE, false)
    }
}