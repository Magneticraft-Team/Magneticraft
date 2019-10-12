package com.cout970.magneticraft.systems.blocks

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.fromBlock
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModuleElectricity
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/30.
 */
object CommonMethods {

    inline fun <reified T> propertyOf(name: String): PropertyEnum<T> where T : IStringSerializable, T : Enum<T> {
        return PropertyEnum.create(name, T::class.java)
    }

    fun pickDefaultBlock(args: PickBlockArgs): ItemStack {
        return ItemStack(args.default.item, 1, 0)
    }

    fun enableAutoConnectWires(args: OnActivatedArgs): Boolean {
        if (args.playerIn.isSneaking && args.playerIn.heldItemMainhand.isEmpty) {

            val handler = MANUAL_CONNECTION_HANDLER!!.fromBlock(args.state.block, args.side)
            val pos = handler?.getBasePos(args.pos, args.worldIn, args.playerIn, args.side, args.heldItem) ?: args.pos

            val te = args.worldIn.getTile<TileBase>(pos) ?: return false
            val electricModule = te.container.modules.find { it is ModuleElectricity } as? ModuleElectricity
                    ?: return false

            electricModule.autoConnectWires = !electricModule.autoConnectWires
            if (!electricModule.autoConnectWires) {
                electricModule.clearWireConnections()
            }
            if (args.worldIn.isServer) {
                if (electricModule.autoConnectWires) {
                    args.playerIn.sendMessage("text.magneticraft.auto_connect.activate")
                } else {
                    args.playerIn.sendMessage("text.magneticraft.auto_connect.deactivate")
                }
            }
            return true
        }
        return false
    }

    fun placeWithFacing(it: OnBlockPlacedArgs): IBlockState {
        return it.defaultValue.withProperty(PROPERTY_FACING, Facing.of(it.facing))
    }

    fun placeWithOppositeFacing(it: OnBlockPlacedArgs): IBlockState {
        return it.defaultValue.withProperty(PROPERTY_FACING, Facing.of(it.facing.opposite))
    }

    fun placeWithOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        return it.defaultValue.withProperty(PROPERTY_ORIENTATION, Orientation.of(placer.horizontalFacing))
    }

    fun placeWithOppositeOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        val orientation = Orientation.of(placer.horizontalFacing.opposite)
        return it.defaultValue.withProperty(PROPERTY_ORIENTATION, orientation)
    }

    fun placeCenterWithOppositeOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        val orientation = CenterOrientation.of(placer.horizontalFacing.opposite, true)
        return it.defaultValue.withProperty(PROPERTY_CENTER_ORIENTATION, orientation)
    }

    fun placeCenterWithOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        val orientation = CenterOrientation.of(placer.horizontalFacing, true)
        return it.defaultValue.withProperty(PROPERTY_CENTER_ORIENTATION, orientation)
    }

    fun placeInactiveWithOppositeOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        val orientation = OrientationActive.of(placer.horizontalFacing.opposite, false)
        return it.defaultValue.withProperty(PROPERTY_ORIENTATION_ACTIVE, orientation)
    }

    fun placeInactiveWithOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        val orientation = OrientationActive.of(placer.horizontalFacing, false)
        return it.defaultValue.withProperty(PROPERTY_ORIENTATION_ACTIVE, orientation)
    }

    fun delegateToModule(args: OnActivatedArgs): Boolean {
        val pos = if (PROPERTY_CENTER_ORIENTATION.isIn(args.state)) {
            val prop = args.state.getValue(PROPERTY_CENTER_ORIENTATION)
            if (prop.center) args.pos else args.pos.offset(prop.facing)
        } else args.pos

        val tile = args.worldIn.getTile<TileBase>(pos) ?: return false
        val methods = tile.container.modules.filterIsInstance<IOnActivated>()
        return methods.any { it.onActivated(args) }
    }

    fun openGui(args: OnActivatedArgs): Boolean {
        return if (!args.playerIn.isSneaking) {
            if (args.worldIn.isServer) {
                args.playerIn.openGui(Magneticraft, -1, args.worldIn, args.pos.xi, args.pos.yi, args.pos.zi)
            }
            true
        } else false
    }

    fun <T> providerFor(cap: () -> Capability<T>?, handler: T): ICapabilityProvider? {
        return object : ICapabilityProvider {

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? = handler as? T

            override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean = capability == cap()
        }
    }

    /**
     * The base value is associated to the NORTH direction
     */
    fun updateBoundingBoxWithFacing(
            base: (BoundingBoxArgs) -> List<AxisAlignedBB>): (BoundingBoxArgs) -> List<AxisAlignedBB> {
        return { args ->
            val boxes = base(args)
            val facing = args.state[PROPERTY_FACING]?.facing ?: EnumFacing.DOWN
            val center = vec3Of(0.5)
            boxes.map { facing.rotateBox(center, it) }
        }
    }

    /**
     * The base value is associated to the NORTH direction
     */
    fun updateBoundingBoxWithOrientation(
            base: (BoundingBoxArgs) -> List<AxisAlignedBB>): (BoundingBoxArgs) -> List<AxisAlignedBB> {
        return { args ->
            val boxes = base(args)
            val facing = args.state[PROPERTY_ORIENTATION]?.facing ?: EnumFacing.NORTH
            val center = vec3Of(0.5)
            boxes.map { facing.rotateBox(center, it) }
        }
    }

//    fun dropWithTileNBT(args: DropsArgs, getNBT: (IBlockAccess, BlockPos) -> NBTTagCompound?): List<ItemStack> {
//        if (args.default.isNotEmpty()) {
//            val stack = args.default[0]
//            val nbt = getNBT(args.world, args.pos)
//
//            if (nbt != null) {
//                stack.setTagInfo("BlockEntityTag", nbt)
//            }
//        }
//        return args.default
//    }

    // Common properties
    val PROPERTY_FACING: PropertyEnum<Facing> =
            PropertyEnum.create("facing", Facing::class.java)

    val PROPERTY_ORIENTATION: PropertyEnum<Orientation> =
            PropertyEnum.create("orientation", Orientation::class.java)

    val PROPERTY_CENTER_ORIENTATION: PropertyEnum<CenterOrientation> =
            PropertyEnum.create("center_orientation", CenterOrientation::class.java)

    val PROPERTY_ORIENTATION_ACTIVE: PropertyEnum<OrientationActive> =
            PropertyEnum.create("orientation_active", OrientationActive::class.java)

    enum class Facing(override val stateName: String,
                      val facing: EnumFacing,
                      override val isVisible: Boolean) : IStatesEnum, IStringSerializable, IRotable<Facing> {

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

        override fun next(): IRotable<Facing> = of(EnumFacing.getFront((facing.index + 1) % EnumFacing.VALUES.size))

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

    enum class Orientation(override val stateName: String,
                           override val isVisible: Boolean,
                           val facing: EnumFacing) : IStatesEnum, IStringSerializable, IRotable<Orientation> {

        NORTH("north", true, EnumFacing.NORTH),
        SOUTH("south", false, EnumFacing.SOUTH),
        EAST("east", false, EnumFacing.EAST),
        WEST("west", false, EnumFacing.WEST);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ORIENTATION, this)
        }

        override fun next(): IRotable<Orientation> = of(facing.rotateY())

        companion object {
            fun of(facing: EnumFacing): Orientation = when (facing) {
                EnumFacing.NORTH -> NORTH
                EnumFacing.SOUTH -> SOUTH
                EnumFacing.WEST -> WEST
                EnumFacing.EAST -> EAST
                else -> NORTH
            }
        }
    }

    enum class CenterOrientation(
            override val stateName: String,
            override val isVisible: Boolean,
            val center: Boolean,
            val facing: EnumFacing) : IStatesEnum, IStringSerializable {

        CENTER_NORTH("center_north", true, false, EnumFacing.NORTH),
        CENTER_SOUTH("center_south", false, false, EnumFacing.SOUTH),
        CENTER_WEST("center_west", false, false, EnumFacing.WEST),
        CENTER_EAST("center_east", false, false, EnumFacing.EAST),
        NO_CENTER_NORTH("no_center_north", false, true, EnumFacing.NORTH),
        NO_CENTER_SOUTH("no_center_south", false, true, EnumFacing.SOUTH),
        NO_CENTER_WEST("no_center_west", false, true, EnumFacing.WEST),
        NO_CENTER_EAST("no_center_east", false, true, EnumFacing.EAST);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_CENTER_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_CENTER_ORIENTATION, this)
        }

        companion object {
            fun of(facing: EnumFacing, center: Boolean) = when {
                facing == EnumFacing.NORTH && !center -> CENTER_NORTH
                facing == EnumFacing.SOUTH && !center -> CENTER_SOUTH
                facing == EnumFacing.WEST && !center -> CENTER_WEST
                facing == EnumFacing.EAST && !center -> CENTER_EAST
                facing == EnumFacing.NORTH && center -> NO_CENTER_NORTH
                facing == EnumFacing.SOUTH && center -> NO_CENTER_SOUTH
                facing == EnumFacing.WEST && center -> NO_CENTER_WEST
                facing == EnumFacing.EAST && center -> NO_CENTER_EAST
                else -> CENTER_NORTH
            }
        }
    }

    enum class OrientationActive(
            override val stateName: String,
            override val isVisible: Boolean,
            val active: Boolean,
            val facing: EnumFacing) : IStatesEnum, IStringSerializable, IRotable<OrientationActive> {

        OFF_NORTH("off_north", true, false, EnumFacing.NORTH),
        OFF_SOUTH("off_south", false, false, EnumFacing.SOUTH),
        OFF_WEST("off_west", false, false, EnumFacing.WEST),
        OFF_EAST("off_east", false, false, EnumFacing.EAST),
        ON_NORTH("on_north", false, true, EnumFacing.NORTH),
        ON_SOUTH("on_south", false, true, EnumFacing.SOUTH),
        ON_WEST("on_west", false, true, EnumFacing.WEST),
        ON_EAST("on_east", false, true, EnumFacing.EAST);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ORIENTATION_ACTIVE)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ORIENTATION_ACTIVE, this)
        }

        override fun next(): IRotable<OrientationActive> = when (this) {
            OFF_NORTH -> OFF_SOUTH
            OFF_SOUTH -> OFF_WEST
            OFF_WEST -> OFF_EAST
            OFF_EAST -> OFF_NORTH
            ON_NORTH -> ON_NORTH
            ON_SOUTH -> ON_SOUTH
            ON_WEST -> ON_WEST
            ON_EAST -> ON_EAST
        }

        companion object {
            fun of(facing: EnumFacing, active: Boolean) = when {
                facing == EnumFacing.NORTH && !active -> OFF_NORTH
                facing == EnumFacing.SOUTH && !active -> OFF_SOUTH
                facing == EnumFacing.WEST && !active -> OFF_WEST
                facing == EnumFacing.EAST && !active -> OFF_EAST
                facing == EnumFacing.NORTH && active -> ON_NORTH
                facing == EnumFacing.SOUTH && active -> ON_SOUTH
                facing == EnumFacing.WEST && active -> ON_WEST
                facing == EnumFacing.EAST && active -> ON_EAST
                else -> OFF_NORTH
            }
        }
    }
}