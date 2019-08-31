package com.cout970.magneticraft.features.electric_conductors

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.api.energy.IManualConnectionHandler.Result.*
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.tileentity.tryConnect
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.itemblocks.ItemBlockElectricPoleTransformer
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.tilemodules.ModuleElectricity
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import com.cout970.magneticraft.systems.tilerenderers.px
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import kotlin.math.min

/**
 * Created by cout970 on 2017/08/10.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    val PROPERTY_POLE_ORIENTATION: PropertyEnum<PoleOrientation> = PropertyEnum.create("pole_orientation", PoleOrientation::class.java)
    val PROPERTY_TESLA_TOWER_PART: PropertyEnum<TeslaTowerPart> = PropertyEnum.create("tesla_tower_part", TeslaTowerPart::class.java)

    lateinit var connector: BlockBase private set
    lateinit var electricPole: BlockBase private set
    lateinit var electricPoleTransformer: BlockBase private set
    lateinit var electricCable: BlockBase private set
    lateinit var teslaTower: BlockBase private set
    lateinit var energyReceiver: BlockBase private set


    // hacky way to avoid power pole drops and break particles, non thread-safe
    var air = false

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        connector = builder.withName("connector").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileConnector)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            alwaysDropDefault = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/connector.mcx"),
                    "inventory" to resource("models/block/mcx/connector.mcx")
            )
            //methods
            boundingBox = CommonMethods.updateBoundingBoxWithFacing {
                listOf(Vec3d(PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 5) createAABBUsing Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, 1.0))
            }
            onBlockPlaced = CommonMethods::placeWithFacing
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::enableAutoConnectWires
            canPlaceBlockOnSide = { it.default && canStayInSide(it.worldIn, it.pos, it.side) }
            capabilityProvider = CommonMethods.providerFor({ MANUAL_CONNECTION_HANDLER }, ConnectorManualConnectionHandler)
            onNeighborChanged = {
                if (!canStayInSide(it.worldIn, it.pos, it.state.getFacing())) {
                    it.worldIn.destroyBlock(it.pos, true)
                }
            }
        }.build()

        electricPole = builder.withName("electric_pole").copy {
            material = Material.WOOD
            states = PoleOrientation.values().toList()
            factoryFilter = { state -> state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false }
            factory = factoryOf(::TileElectricPole)
            alwaysDropDefault = true
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_pole.mcx"),
                    "inventory" to resource("models/block/mcx/electric_pole_inv.mcx")
            )
            boundingBox = {
                val size = 0.0625 * 3
                listOf(Vec3d(0.5 - size, 0.0, 0.5 - size) createAABBUsing Vec3d(0.5 + size, 1.0, 0.5 + size))
            }
            onBlockBreak = Blocks::breakElectricPole
            blockStatesToPlace = Blocks::placeElectricPole
            onDrop = {
                if (it.state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() == true)
                    it.default
                else
                    emptyList()
            }
            capabilityProvider = CommonMethods.providerFor({ MANUAL_CONNECTION_HANDLER }, ElectricPoleManualConnectionHandler)
            onActivated = CommonMethods::enableAutoConnectWires
        }.build()

        electricPoleTransformer = builder.withName("electric_pole_transformer").copy {
            material = Material.WOOD
            states = PoleOrientation.values().toList()
            factoryFilter = { state -> state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false }
            factory = factoryOf(::TileElectricPoleTransformer)
            alwaysDropDefault = true
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_pole_transformer.mcx"),
                    "inventory" to resource("models/block/mcx/electric_pole_transformer_inv.mcx")
            )
            boundingBox = {
                val size = 0.0625 * 3
                listOf(Vec3d(0.5 - size, 0.0, 0.5 - size) createAABBUsing Vec3d(0.5 + size, 1.0, 0.5 + size))
            }
            onBlockBreak = Blocks::breakElectricPole
            onDrop = {
                if (it.state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() == true) it.default + electricPole.stack()
                else emptyList()
            }
            capabilityProvider = CommonMethods.providerFor({ MANUAL_CONNECTION_HANDLER },
                    ElectricPoleManualConnectionHandler)
            onActivated = CommonMethods::enableAutoConnectWires
        }.build()

        electricCable = builder.withName("electric_cable").copy {
            factory = factoryOf(::TileElectricCable)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_cable.mcx"),
                    "inventory" to resource("models/block/mcx/electric_cable.mcx")
            )
            boundingBox = { cableBoundingBox(it.source, it.pos, 6) }
        }.build()

        teslaTower = builder.withName("tesla_tower").copy {
            states = TeslaTowerPart.values().toList()
            factory = { _, state ->
                if (state[PROPERTY_TESLA_TOWER_PART] == TeslaTowerPart.BOTTOM) TileTeslaTower() else TileTeslaTowerPart()
            }
            hasCustomModel = true
            forceModelBake = true
            generateDefaultItemBlockModel = false
            customModels = listOf(
                    "bottom" to resource("models/block/gltf/tesla_tower.gltf"),
                    "inventory" to resource("models/block/gltf/tesla_tower_inv.gltf")
            )

            onActivated = {
                val part = it.state[PROPERTY_TESLA_TOWER_PART]
                if (part == Blocks.TeslaTowerPart.BOTTOM) {
                    CommonMethods.openGui(it)
                } else {
                    val state = it.worldIn.getBlockState(it.pos.down())
                    state.block.onBlockActivated(it.worldIn, it.pos.down(), state, it.playerIn, it.hand, it.side,
                            it.hit.xf, it.hit.yf, it.hit.zf
                    )
                }
            }
            pickBlock = CommonMethods::pickDefaultBlock
            blockStatesToPlace = {
                val bottom = TeslaTowerPart.BOTTOM.getBlockState(it.default.block)
                val middle = TeslaTowerPart.MIDDLE.getBlockState(it.default.block)
                val top = TeslaTowerPart.TOP.getBlockState(it.default.block)

                listOf(BlockPos.ORIGIN to bottom, BlockPos(0, 1, 0) to middle, BlockPos(0, 2, 0) to top)
            }
            onBlockBreak = {
                val part = it.state[PROPERTY_TESLA_TOWER_PART]
                when (part) {
                    TeslaTowerPart.BOTTOM -> {
                        it.worldIn.destroyBlock(it.pos + EnumFacing.UP, false)
                        it.worldIn.destroyBlock(it.pos + EnumFacing.UP + EnumFacing.UP, false)
                    }
                    TeslaTowerPart.MIDDLE -> it.worldIn.destroyBlock(it.pos + EnumFacing.DOWN, true)
                    TeslaTowerPart.TOP -> it.worldIn.destroyBlock(it.pos + EnumFacing.DOWN + EnumFacing.DOWN, true)
                }
            }
            onDrop = {
                val center = it.state[PROPERTY_TESLA_TOWER_PART] == TeslaTowerPart.BOTTOM
                if (center) it.default else emptyList()
            }
        }.build()

        energyReceiver = builder.withName("energy_receiver").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileEnergyReceiver)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/gltf/energy_receiver.gltf"),
                    "inventory" to resource("models/block/gltf/energy_receiver.gltf")
            )
            boundingBox = CommonMethods.updateBoundingBoxWithFacing {
                listOf(Vec3d(PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 8) createAABBUsing Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, 1.0))
            }
            onBlockPlaced = CommonMethods::placeWithFacing
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        return itemBlockListOf(connector, electricPole, electricCable, teslaTower, energyReceiver) +
                (electricPoleTransformer to ItemBlockElectricPoleTransformer(electricPoleTransformer))
    }

    fun cableBoundingBox(world: IBlockAccess, pos: BlockPos, size: Int): List<AABB> {
        val mod = world.getTile<TileElectricCable>(pos)
        val list = mutableListOf<AABB>()

        list += vec3Of(size.px) createAABBUsing vec3Of(1 - size.px)

        if (mod != null) {
            if (mod.canConnect(EnumFacing.DOWN))
                list += vec3Of(size.px, 0, size.px) createAABBUsing vec3Of(1 - size.px, size.px, 1 - size.px)

            if (mod.canConnect(EnumFacing.UP))
                list += vec3Of(size.px, 1 - size.px, size.px) createAABBUsing vec3Of(1 - size.px, 1, 1 - size.px)

            if (mod.canConnect(EnumFacing.NORTH))
                list += vec3Of(size.px, size.px, 0) createAABBUsing vec3Of(1 - size.px, 1 - size.px, size.px)

            if (mod.canConnect(EnumFacing.SOUTH))
                list += vec3Of(size.px, size.px, 1 - size.px) createAABBUsing vec3Of(1 - size.px, 1 - size.px, 1)

            if (mod.canConnect(EnumFacing.WEST))
                list += vec3Of(0, size.px, size.px) createAABBUsing vec3Of(size.px, 1 - size.px, 1 - size.px)

            if (mod.canConnect(EnumFacing.EAST))
                list += vec3Of(1 - size.px, size.px, size.px) createAABBUsing vec3Of(1, 1 - size.px, 1 - size.px)
        }

        return list
    }

    fun breakElectricPole(args: BreakBlockArgs): Unit = args.run {
        if (state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() == true) {
            for (i in 1..4) {
                if (air) {
                    worldIn.setBlockToAir(pos.offset(EnumFacing.DOWN, i))
                } else {
                    worldIn.destroyBlock(pos.offset(EnumFacing.DOWN, i), false)
                }
            }
        } else {
            val newPos = PoleOrientation.getMainPos(state, pos)
            if (air) {
                worldIn.setBlockToAir(newPos)
            } else {
                worldIn.destroyBlock(newPos, true)
            }
        }
    }

    fun placeElectricPole(args: BlockStatesToPlaceArgs): List<Pair<BlockPos, IBlockState>> = args.run {
        val yaw = when {
            player.rotationYaw >= 180 -> player.rotationYaw - 360
            player.rotationYaw <= -180 -> player.rotationYaw + 360
            else -> player.rotationYaw
        }
        val a = 45
        val b = 45 / 2
        //@formatter:off
        val dir = when {
            yaw < -a * 3 + b && yaw >= -a * 4 + b -> PoleOrientation.NORTH_EAST
            yaw < -a * 2 + b && yaw >= -a * 3 + b -> PoleOrientation.EAST
            yaw < -a + b && yaw >= -a * 2 + b -> PoleOrientation.SOUTH_EAST
            yaw < 0 + b && yaw >= -a + b -> PoleOrientation.SOUTH
            yaw < a + b && yaw >= 0 + b -> PoleOrientation.SOUTH_WEST
            yaw < a * 2 + b && yaw >= a + b -> PoleOrientation.WEST
            yaw < a * 3 + b && yaw >= a * 2 + b -> PoleOrientation.NORTH_WEST
            yaw < a * 4 + b && yaw >= a * 3 + b -> PoleOrientation.NORTH
            else -> PoleOrientation.NORTH
        }
        //@formatter:on

        val pos = BlockPos.ORIGIN
        listOf(
                pos to default.withProperty(PROPERTY_POLE_ORIENTATION, PoleOrientation.DOWN_4),
                pos.offset(EnumFacing.UP, 1) to default.withProperty(PROPERTY_POLE_ORIENTATION, PoleOrientation.DOWN_3),
                pos.offset(EnumFacing.UP, 2) to default.withProperty(PROPERTY_POLE_ORIENTATION, PoleOrientation.DOWN_2),
                pos.offset(EnumFacing.UP, 3) to default.withProperty(PROPERTY_POLE_ORIENTATION, PoleOrientation.DOWN_1),
                pos.offset(EnumFacing.UP, 4) to default.withProperty(PROPERTY_POLE_ORIENTATION, dir)
        )
    }

    enum class TeslaTowerPart(
            override val stateName: String,
            override val isVisible: Boolean
    ) : IStatesEnum, IStringSerializable {
        BOTTOM("bottom", true),
        MIDDLE("middle", false),
        TOP("top", false);

        override fun getName() = name.toLowerCase()

        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_TESLA_TOWER_PART)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_TESLA_TOWER_PART, this)
        }
    }

    enum class PoleOrientation(
            override val stateName: String,
            override val isVisible: Boolean,
            val offset: Vec3d,
            val angle: Float = 0f,
            val offsetY: Int = 0
    ) : IStatesEnum, IStringSerializable {

        NORTH("north", true, Vec3d(1.0, 0.0, 0.0), 180f),
        NORTH_EAST("north_east", false, Vec3d(0.707106, 0.0, 0.707106), -45f + 180f),
        EAST("east", false, Vec3d(0.0, 0.0, 1.0), 90f),
        SOUTH_EAST("south_east", false, Vec3d(-0.707106, 0.0, 0.707106), 45f),
        SOUTH("south", false, Vec3d(-1.0, 0.0, 0.0), 0f),
        SOUTH_WEST("south_west", false, Vec3d(-0.707106, 0.0, -0.707106), -90f + 45),
        WEST("west", false, Vec3d(0.0, 0.0, -1.0), -90f),
        NORTH_WEST("north_west", false, Vec3d(0.707106, 0.0, -0.707106), 45f + 180),
        DOWN_1("down_1", false, Vec3d.ZERO, offsetY = 1),
        DOWN_2("down_2", false, Vec3d.ZERO, offsetY = 2),
        DOWN_3("down_3", false, Vec3d.ZERO, offsetY = 3),
        DOWN_4("down_4", false, Vec3d.ZERO, offsetY = 4);

        fun isMainBlock() = offsetY == 0

        override fun getName() = name.toLowerCase()

        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_POLE_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_POLE_ORIENTATION, this)
        }

        companion object {
            fun getMainPos(state: IBlockState, pos: BlockPos): BlockPos {
                return when (state[PROPERTY_POLE_ORIENTATION]) {
                    DOWN_1 -> pos.offset(EnumFacing.UP, 1)
                    DOWN_2 -> pos.offset(EnumFacing.UP, 2)
                    DOWN_3 -> pos.offset(EnumFacing.UP, 3)
                    DOWN_4 -> pos.offset(EnumFacing.UP, 4)
                    else -> pos
                }
            }
        }
    }

    object ConnectorManualConnectionHandler : IManualConnectionHandler {

        override fun getBasePos(thisBlock: BlockPos, world: World, player: EntityPlayer, side: EnumFacing,
                                stack: ItemStack): BlockPos {
            return thisBlock
        }

        override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer,
                                 side: EnumFacing, stack: ItemStack): IManualConnectionHandler.Result {

            val tile = world.getTile<TileConnector>(thisBlock)
            val other = world.getTileEntity(otherBlock)
            if (tile == null || other == null) {
                return ERROR
            }
            val handler = other.getOrNull(ELECTRIC_NODE_HANDLER, side)
                    ?: return NOT_A_CONNECTOR
            val otherNodes = handler.nodes.filterIsInstance(IWireConnector::class.java)


            return inferResult(tile.electricModule, listOf(tile.wrapper), otherNodes, handler)
        }
    }

    object ElectricPoleManualConnectionHandler : IManualConnectionHandler {

        override fun getBasePos(thisBlock: BlockPos, world: World, player: EntityPlayer, side: EnumFacing,
                                stack: ItemStack): BlockPos {
            val state = world.getBlockState(thisBlock)
            val orientation = state[PROPERTY_POLE_ORIENTATION] ?: return thisBlock
            return if (orientation.isMainBlock()) thisBlock else thisBlock.offset(EnumFacing.UP, orientation.offsetY)
        }

        override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer,
                                 side: EnumFacing, stack: ItemStack): IManualConnectionHandler.Result {

            val pos = getBasePos(thisBlock, world, player, side, stack)
            val tile = world.getTileEntity(pos)
            val other = world.getTileEntity(otherBlock)
            if (tile == null || other == null) {
                return ERROR
            }
            val handler = other.getOrNull(ELECTRIC_NODE_HANDLER, side) ?: return NOT_A_CONNECTOR

            val otherNodes = handler.nodes.filterIsInstance(IWireConnector::class.java)

            val module = when (tile) {
                is TileElectricPole -> tile.electricModule
                is TileElectricPoleTransformer -> tile.electricModule
                else -> return INVALID_CONNECTOR
            }

            return inferResult(module, module.electricNodes, otherNodes, handler)
        }
    }

    private fun inferResult(module: ModuleElectricity, thisNodes: List<IElectricNode>, otherNodes: List<IElectricNode>, handler: IElectricNodeHandler): IManualConnectionHandler.Result {
        var same = false
        var tooFar = false
        var already = false
        val size = module.outputWiredConnections.size
        thisNodes.forEach { thisNode ->
            otherNodes.forEach { otherNode ->
                val dist = Math.sqrt(otherNode.pos.distanceSq(thisNode.pos))
                if (thisNode === otherNode || module === handler) same = true
                if (handler is ModuleElectricity) {
                    if (module.inputWiredConnections.any { it.firstNode == otherNode } || handler.inputWiredConnections.any { it.firstNode == thisNode }) {
                        already = true
                    }

                    if (module.outputWiredConnections.any { it.secondNode == otherNode } || handler.outputWiredConnections.any { it.secondNode == thisNode }) {
                        already = true
                    }

                    if (min(module.maxWireDistance, handler.maxWireDistance) < dist) tooFar = true
                }

                tryConnect(module, thisNode, handler, otherNode, null)
            }
        }

        return if (size != module.outputWiredConnections.size) {
            SUCCESS
        } else {
            when {
                already -> ALREADY_CONNECTED
                tooFar -> TOO_FAR
                same -> SAME_CONNECTOR
                else -> ERROR
            }
        }
    }

    fun canStayInSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        if (worldIn.getBlockState(pos.offset(side.opposite)).block == electricCable) return true

        if (worldIn.isSideSolid(pos.offset(side.opposite), side.opposite, false)) return true

        var box = Vec3d(0.5 - PIXEL, 0.5 - PIXEL, 0.5 - PIXEL) createAABBUsing Vec3d(0.5 + PIXEL, 0.5 + PIXEL, 0.5 + PIXEL)
        val temp = side.opposite.directionVec.toVec3d() * 0.625 + Vec3d(0.5, 0.5, 0.5)
        val blockPos = pos.offset(side.opposite)

        box = box.union(temp createAABBUsing temp).offset(pos)
        val state = worldIn.getBlockState(blockPos)
        val list = mutableListOf<AxisAlignedBB>()

        state.addCollisionBoxToList(worldIn, blockPos, box, list, null, false)
        return list.isNotEmpty()
    }
}