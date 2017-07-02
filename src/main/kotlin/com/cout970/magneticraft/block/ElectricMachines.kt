package com.cout970.magneticraft.block

import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.core.*
import com.cout970.magneticraft.item.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.tileentity.tryConnect
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileCoalGenerator
import com.cout970.magneticraft.tileentity.TileConnector
import com.cout970.magneticraft.tileentity.TileElectricFurnace
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

    lateinit var connector: BlockBase private set
    lateinit var battery: BlockBase private set
    lateinit var electric_furnace: BlockBase private set
    lateinit var coal_generator: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        connector = builder.withName("connector").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileConnector)
            overrideItemModel = false
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
            overrideItemModel = false
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
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileElectricFurnace)
            alwaysDropDefault = true
            overrideItemModel = false
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
            factory = { _, state -> if(state[PROPERTY_ORIENTATION_AND_LEVEL] != OrientationAndLevel.UP) TileCoalGenerator() else null }
            alwaysDropDefault = true
            overrideItemModel = false
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
                    it.worldIn.destroyBlock(it.pos.down(), false)
                } else {
                    it.worldIn.destroyBlock(it.pos.up(), false)
                }
            }
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        return itemBlockListOf(connector, battery, electric_furnace, coal_generator)
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