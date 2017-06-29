package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.*
import com.cout970.magneticraft.item.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.TileConnector
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.toAABBWith
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentTranslation

/**
 * Created by cout970 on 2017/06/29.
 */
object ElectricMachines : IBlockMaker {

    val PROPERTY_FACING = PropertyEnum.create("facing", Facing::class.java)!!

    lateinit var connector: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        connector = builder.withName("connector").copy {
            states = Facing.values().toList()
            factory = factoryOf(::TileConnector)
            overrideItemModel = false
            customModels = listOf(
                    "model" to resource("models/block/mcx/connector.mcx"),
                    "inventory" to resource("models/block/mcx/connector.mcx")
            )
            enableOcclusionOptimization = false
            translucent = true
            //methods
            boundingBox = ElectricMachines::getBoundingBox
            onBlockPlaced = { it.defaultValue.withProperty(PROPERTY_FACING, Facing.of(it.facing)) }
            pickBlock = { ItemStack(it.default.item, 1, Facing.DOWN.ordinal) }
            onActivated = ElectricMachines::onActivated
        }.build()

        return itemBlockListOf(connector)
    }

//    override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer,
//                             side: EnumFacing, stack: ItemStack): Boolean {
//        val tile = world.getTile<TileElectricConnector>(thisBlock)
//        val other = world.getTileEntity(otherBlock)
//        if (tile == null || other == null) {
//            return false
//        }
//        val handler = ELECTRIC_NODE_HANDLER!!.fromTile(other) ?: return false
//        return tile.connectWire(handler, side)
//    }
//
//    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
//        return super.canPlaceBlockOnSide(worldIn, pos, side) && canStayInSide(worldIn, pos, side.opposite)
//    }
//
//    //pos, block to place the connector
//    //side PROPERTY_FACING in the connector
//    fun canStayInSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
//        if (worldIn.isSideSolid(pos.offset(side), side.opposite, false)) return true
//
//        var box = Vec3d(0.5 - PIXEL, 0.5 - PIXEL, 0.5 - PIXEL) toAABBWith Vec3d(0.5 + PIXEL, 0.5 + PIXEL, 0.5 + PIXEL)
//        val temp = side.directionVec.toVec3d() * 0.625 + Vec3d(0.5, 0.5, 0.5)
//        val blockPos = pos.offset(side)
//
//        box = box.union(temp toAABBWith temp).offset(pos)
//        val state = worldIn.getBlockState(blockPos)
//        val list = mutableListOf<AxisAlignedBB>()
//
//        state.addCollisionBoxToList(worldIn, blockPos, box, list, null)
//        return list.isNotEmpty()
//    }
//
//    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, blockIn: Block?) {
//        val dir = state[PROPERTY_FACING]
//        if (!canStayInSide(world, pos, dir)) {
//            world.destroyBlock(pos, true)
//        }
//        super.neighborChanged(state, world, pos, blockIn)
//    }

    fun onActivated(args: OnActivatedArgs): Boolean {
        if (args.playerIn.isSneaking && args.playerIn.heldItemMainhand == null) {
            val te = args.worldIn.getTile<TileConnector>(args.pos) ?: return false

            te.electricModule.autoConnectWires = !te.electricModule.autoConnectWires
            if (!te.electricModule.autoConnectWires) {
                te.electricModule.clearWireConnections()
            }
            if (args.worldIn.isServer) {
                if (te.electricModule.autoConnectWires) {
                    args.playerIn.sendStatusMessage(
                            TextComponentTranslation("text.magneticraft.auto_connect.activate"), false)
                } else {
                    args.playerIn.sendStatusMessage(
                            TextComponentTranslation("text.magneticraft.auto_connect.deactivate"), false)
                }
            }
            return true
        }
        return false
    }

    fun getBoundingBox(args: BoundingBoxArgs): AxisAlignedBB {
        val facing = args.state[PROPERTY_FACING]?.facing ?: EnumFacing.DOWN
        return when (facing.opposite) {
            EnumFacing.DOWN -> {
                Vec3d(PIXEL * 5, 0.0, PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 5)
            }
            EnumFacing.UP -> {
                Vec3d(PIXEL * 5, 1.0 - PIXEL * 5, PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0, 1.0 - PIXEL * 5)
            }
            EnumFacing.NORTH -> {
                Vec3d(PIXEL * 5, PIXEL * 5, 0.0) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, PIXEL * 5)
            }
            EnumFacing.SOUTH -> {
                Vec3d(PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, 1.0)
            }
            EnumFacing.WEST -> {
                Vec3d(0.0, PIXEL * 5, PIXEL * 5) toAABBWith Vec3d(PIXEL * 5, 1.0 - PIXEL * 5, 1.0 - PIXEL * 5)
            }
            else -> {
                Vec3d(1 - PIXEL * 5, PIXEL * 5, PIXEL * 5) toAABBWith Vec3d(1.0, 1.0 - PIXEL * 5, 1.0 - PIXEL * 5)
            }
        }
    }

    enum class Facing(override val stateName: String,
                      val facing: EnumFacing,
                      override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        DOWN("down", EnumFacing.DOWN, true),
        UP("up", EnumFacing.UP, false),
        NORTH("north", EnumFacing.NORTH, false),
        SOUTH("south", EnumFacing.SOUTH, false),
        EAST("east", EnumFacing.EAST, false),
        WEST("west", EnumFacing.WEST, false);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_FACING)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_FACING, this)
        }

        companion object {
            fun of(facing: EnumFacing): Facing = when (facing) {
                EnumFacing.DOWN -> DOWN
                EnumFacing.UP -> UP
                EnumFacing.NORTH -> NORTH
                EnumFacing.SOUTH -> SOUTH
                EnumFacing.WEST -> WEST
                EnumFacing.EAST -> EAST
            }
        }
    }
}