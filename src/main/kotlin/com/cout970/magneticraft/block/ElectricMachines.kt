package com.cout970.magneticraft.block

import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.core.*
import com.cout970.magneticraft.item.itemblock.ItemBlockElectricPoleTransformer
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.tileentity.tryConnect
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.times
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.toVec3d
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
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/06/29.
 */
object ElectricMachines : IBlockMaker {

    val PROPERTY_ORIENTATION_AND_LEVEL = PropertyEnum.create("orientation_and_level", OrientationAndLevel::class.java)!!
    val PROPERTY_POLE_ORIENTATION = PropertyEnum.create("pole_orientation", PoleOrientation::class.java)!!

    lateinit var connector: BlockBase private set
    lateinit var battery: BlockBase private set
    lateinit var electric_furnace: BlockBase private set
    lateinit var coal_generator: BlockBase private set
    lateinit var electric_pole: BlockBase private set
    lateinit var electric_pole_transformer: BlockBase private set

    // hacky way to avoid power pole drops and break particles
    var air = false

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        connector = builder.withName("connector").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileConnector)
            generateDefaultItemModel = false
            hasCustomModel = true
            alwaysDropDefault = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/connector.mcx"),
                    "inventory" to resource("models/block/mcx/connector.mcx")
            )
            //methods
            boundingBox = CommonMethods.updateBoundingBoxWithFacing(
                    Vec3d(PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, 1.0)
            )
            onBlockPlaced = CommonMethods::placeWithFacing
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::enableAutoConnectWires
            canPlaceBlockOnSide = { it.default && canStayInSide(it.worldIn, it.pos, it.side) }
            capabilityProvider = CommonMethods.providerFor(MANUAL_CONNECTION_HANDLER, ConnectorManualConnectionHandler)
            onNeighborChanged = {
                if (!canStayInSide(it.worldIn, it.pos, it.state.getFacing())) {
                    it.worldIn.destroyBlock(it.pos, true)
                }
            }
        }.build()

        battery = builder.withName("battery").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileBattery)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/battery.mcx"),
                    "inventory" to resource("models/block/mcx/battery.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        electric_furnace = builder.withName("electric_furnace").copy {
            material = Material.ROCK
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileElectricFurnace)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_furnace.mcx"),
                    "inventory" to resource("models/block/mcx/electric_furnace.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        coal_generator = builder.withName("coal_generator").copy {
            states = OrientationAndLevel.values().toList()
            factoryFilter = { state -> state[PROPERTY_ORIENTATION_AND_LEVEL] != OrientationAndLevel.UP }
            factory = factoryOf(::TileCoalGenerator)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/coal_generator.mcx"),
                    "inventory" to resource("models/block/mcx/coal_generator_inv.mcx")
            )
            //methods
            blockStatesToPlace = {
                val orientation = OrientationAndLevel.of(it.player.horizontalFacing)
                val base = it.default.withProperty(PROPERTY_ORIENTATION_AND_LEVEL, orientation)
                val up = it.default.withProperty(PROPERTY_ORIENTATION_AND_LEVEL, OrientationAndLevel.UP)
                listOf(BlockPos.ORIGIN to base, BlockPos.ORIGIN.up() to up)
            }
            onBlockBreak = {
                if (it.state[PROPERTY_ORIENTATION_AND_LEVEL] == OrientationAndLevel.UP) {
                    it.worldIn.destroyBlock(it.pos.down(), true)
                } else {
                    it.worldIn.destroyBlock(it.pos.up(), true)
                }
            }
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        electric_pole = builder.withName("electric_pole").copy {
            material = Material.WOOD
            states = PoleOrientation.values().toList()
            factoryFilter = { state -> state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false }
            factory = factoryOf(::TileElectricPole)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_pole.mcx"),
                    "inventory" to resource("models/block/mcx/electric_pole_inv.mcx")
            )
            boundingBox = {
                val size = 0.0625 * 3
                Vec3d(0.5 - size, 0.0, 0.5 - size) toAABBWith Vec3d(0.5 + size, 1.0, 0.5 + size)
            }
            onBlockBreak = ElectricMachines::breakElectricPole
            blockStatesToPlace = ElectricMachines::placeElectricPole
            onDrop = {
                if (it.state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false)
                    it.default
                else
                    emptyList()
            }
            capabilityProvider = CommonMethods.providerFor(MANUAL_CONNECTION_HANDLER,
                    ElectricPoleManualConnectionHandler)
            onActivated = CommonMethods::enableAutoConnectWires
        }.build()

        electric_pole_transformer = builder.withName("electric_pole_transformer").copy {
            material = Material.WOOD
            states = PoleOrientation.values().toList()
            factoryFilter = { state -> state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false }
            factory = factoryOf(::TileElectricPoleTransformer)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_pole_transformer.mcx"),
                    "inventory" to resource("models/block/mcx/electric_pole_transformer_inv.mcx")
            )
            boundingBox = {
                val size = 0.0625 * 3
                Vec3d(0.5 - size, 0.0, 0.5 - size) toAABBWith Vec3d(0.5 + size, 1.0, 0.5 + size)
            }
            onBlockBreak = ElectricMachines::breakElectricPole
            onDrop = {
                if (it.state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false) it.default + electric_pole.stack()
                else emptyList()
            }
            capabilityProvider = CommonMethods.providerFor(MANUAL_CONNECTION_HANDLER,
                    ElectricPoleManualConnectionHandler)
            onActivated = CommonMethods::enableAutoConnectWires
        }.build()

        return itemBlockListOf(connector, battery, electric_furnace, coal_generator, electric_pole) +
               (electric_pole_transformer to ItemBlockElectricPoleTransformer(electric_pole_transformer))
    }

    fun breakElectricPole(args: BreakBlockArgs): Unit = args.run {
        if (state[PROPERTY_POLE_ORIENTATION]?.isMainBlock() ?: false) {
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
        val yaw = if (player.rotationYaw >= 180) {
            player.rotationYaw - 360
        } else if (player.rotationYaw <= -180) {
            player.rotationYaw + 360
        } else {
            player.rotationYaw
        }
        val a = 45
        val b = 45 / 2
        //@formatter:off
        val dir = when {
            yaw < -a * 3 + b && yaw >= -a * 4 + b -> PoleOrientation.NORTH_EAST
            yaw < -a * 2 + b && yaw >= -a * 3 + b -> PoleOrientation.EAST
            yaw < -a + b     && yaw >= -a * 2 + b -> PoleOrientation.SOUTH_EAST
            yaw < 0 + b      && yaw >= -a + b     -> PoleOrientation.SOUTH
            yaw < a + b      && yaw >= 0 + b      -> PoleOrientation.SOUTH_WEST
            yaw < a * 2 + b  && yaw >= a + b      -> PoleOrientation.WEST
            yaw < a * 3 + b  && yaw >= a * 2 + b  -> PoleOrientation.NORTH_WEST
            yaw < a * 4 + b  && yaw >= a * 3 + b  -> PoleOrientation.NORTH
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
                    PoleOrientation.DOWN_1 -> pos.offset(EnumFacing.UP, 1)
                    PoleOrientation.DOWN_2 -> pos.offset(EnumFacing.UP, 2)
                    PoleOrientation.DOWN_3 -> pos.offset(EnumFacing.UP, 3)
                    PoleOrientation.DOWN_4 -> pos.offset(EnumFacing.UP, 4)
                    else -> pos
                }
            }
        }
    }

    enum class OrientationAndLevel(
            override val stateName: String,
            override val isVisible: Boolean,
            val facing: EnumFacing,
            val down: Boolean
    ) : IStatesEnum, IStringSerializable {

        NORTH("north", true, EnumFacing.NORTH, true),
        SOUTH("south", false, EnumFacing.SOUTH, true),
        EAST("east", false, EnumFacing.EAST, true),
        WEST("west", false, EnumFacing.WEST, true),
        UP("up", false, EnumFacing.UP, false);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ORIENTATION_AND_LEVEL)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ORIENTATION_AND_LEVEL, this)
        }

        companion object {
            fun of(facing: EnumFacing): OrientationAndLevel = when (facing) {
                EnumFacing.NORTH -> OrientationAndLevel.NORTH
                EnumFacing.SOUTH -> OrientationAndLevel.SOUTH
                EnumFacing.WEST -> OrientationAndLevel.WEST
                EnumFacing.EAST -> OrientationAndLevel.EAST
                else -> OrientationAndLevel.UP
            }
        }
    }

    object ConnectorManualConnectionHandler : IManualConnectionHandler {

        override fun getBasePos(thisBlock: BlockPos, world: World, player: EntityPlayer, side: EnumFacing,
                                stack: ItemStack): BlockPos {
            return thisBlock
        }

        override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer,
                                 side: EnumFacing, stack: ItemStack): Boolean {

            val tile = world.getTile<TileConnector>(thisBlock)
            val other = world.getTileEntity(otherBlock)
            if (tile == null || other == null) {
                return false
            }
            val handler = other.getOrNull(ELECTRIC_NODE_HANDLER, side) ?: return false
            val otherNodes = handler.nodes.filterIsInstance(IWireConnector::class.java)

            val size = tile.electricModule.outputWiredConnections.size
            otherNodes.forEach { otherNode ->
                tryConnect(tile.electricModule, tile.wrapper, handler, otherNode, null)
            }
            return size != tile.electricModule.outputWiredConnections.size
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
                                 side: EnumFacing, stack: ItemStack): Boolean {

            val pos = getBasePos(thisBlock, world, player, side, stack)
            val tile = world.getTileEntity(pos)
            val other = world.getTileEntity(otherBlock)
            if (tile == null || other == null) {
                return false
            }
            val handler = other.getOrNull(ELECTRIC_NODE_HANDLER, side) ?: return false
            val otherNodes = handler.nodes.filterIsInstance(IWireConnector::class.java)

            val module = when (tile) {
                is TileElectricPole -> tile.electricModule
                is TileElectricPoleTransformer -> tile.electricModule
                else -> return false
            }
            val size = module.outputWiredConnections.size
            module.electricNodes.forEach { thisNode ->
                otherNodes.forEach { otherNode ->
                    tryConnect(module, thisNode, handler, otherNode, null)
                }
            }
            return size != module.outputWiredConnections.size
        }
    }

    fun canStayInSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        if (worldIn.isSideSolid(pos.offset(side.opposite), side.opposite, false)) return true

        var box = Vec3d(0.5 - PIXEL, 0.5 - PIXEL, 0.5 - PIXEL) toAABBWith Vec3d(0.5 + PIXEL, 0.5 + PIXEL, 0.5 + PIXEL)
        val temp = side.opposite.directionVec.toVec3d() * 0.625 + Vec3d(0.5, 0.5, 0.5)
        val blockPos = pos.offset(side.opposite)

        box = box.union(temp toAABBWith temp).offset(pos)
        val state = worldIn.getBlockState(blockPos)
        val list = mutableListOf<AxisAlignedBB>()

        state.addCollisionBoxToList(worldIn, blockPos, box, list, null, false)
        return list.isNotEmpty()
    }
}