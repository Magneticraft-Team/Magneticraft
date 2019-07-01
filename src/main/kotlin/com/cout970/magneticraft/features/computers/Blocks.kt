package com.cout970.magneticraft.features.computers

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.itemblocks.blockListOf
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.tilerenderers.px
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/07/07.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    val PROPERTY_ROBOT_ORIENTATION: PropertyEnum<RobotOrientation> = PropertyEnum.create("robot_orientation", RobotOrientation::class.java)

    lateinit var computer: BlockBase private set
    lateinit var miningRobot: BlockBase private set
    lateinit var movingRobot: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock?>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
            alwaysDropDefault = true
        }

        computer = builder.withName("computer").copy {
            factory = factoryOf(::TileComputer)
            states = CommonMethods.Orientation.values().toList()
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/computer.mcx"),
                "inventory" to resource("models/block/mcx/computer.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
            canConnectRedstone = { true }
            redstonePower = {
                it.world.getTile<TileComputer>(it.pos)?.redstoneSensor?.outputs?.get(it.side.ordinal) ?: 0
            }
        }.build()

        miningRobot = builder.withName("mining_robot").copy {
            factory = factoryOf(::TileMiningRobot)
            states = RobotOrientation.values().toList()
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/mining_robot.mcx"),
                "inventory" to resource("models/block/mcx/mining_robot.mcx")
            )
            //methods
            onBlockPlaced = Blocks::placeWithRobotOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
            boundingBox = { it.robotAABBs() }
            canConnectRedstone = { true }
            redstonePower = {
                it.world.getTile<TileMiningRobot>(it.pos)?.redstoneSensor?.outputs?.get(it.side.ordinal) ?: 0
            }

        }.build()

        movingRobot = builder.withName("moving_robot").copy {
            onDrop = { emptyList() }
            boundingBox = { listOf(vec3Of(4.px, 4.px, 4.px) createAABBUsing vec3Of(1 - 4.px, 1 - 4.px, 1 - 4.px)) }
            onActivated = func@{
                for (side in EnumFacing.VALUES) {
                    val state = it.worldIn.getBlockState(it.pos.offset(side))
                    if (state.block == miningRobot) {
                        return@func state.block.onBlockActivated(
                            it.worldIn, it.pos.offset(side), it.state, it.playerIn, it.hand, it.side,
                            it.hit.xf, it.hit.yf, it.hit.zf
                        )
                    }
                }
                false
            }
            pickBlock = { ItemStack(miningRobot) }
            generateDefaultItemBlockModel = false
            enableOcclusionOptimization = false
            translucent = true
        }.build()

        return itemBlockListOf(computer, miningRobot) + blockListOf(movingRobot)
    }

    fun placeWithRobotOrientation(it: OnBlockPlacedArgs): IBlockState {
        return it.defaultValue.withProperty(PROPERTY_ROBOT_ORIENTATION,
            RobotOrientation.of(it.placer?.horizontalFacing
                ?: EnumFacing.NORTH))
    }

    fun BoundingBoxArgs.robotAABBs(): List<AABB> {
        val ori = state[PROPERTY_ROBOT_ORIENTATION]
            ?: return listOf(AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))

        return listOf(
            vec3Of(4.px, 4.px, 6.px) createAABBUsing vec3Of(1 - 4.px, 1 - 4.px, 1 - 2.px), // center
            vec3Of(5.px, 5.px, 1 - 2.px) createAABBUsing vec3Of(1 - 5.px, 1 - 5.px, 1), // main thruster
            vec3Of(5.px, 5.px, 0) createAABBUsing vec3Of(1 - 5.px, 1 - 5.px, 6.px) // dill
        ).map { ori.facing.rotateBox(vec3Of(0.5), it) }
    }

    enum class OrientationLevel {
        UP,
        CENTER,
        DOWN;

        fun up() = if (this == DOWN) CENTER else UP
        fun down() = if (this == UP) CENTER else DOWN
    }

    enum class RobotOrientation(val level: OrientationLevel,
                                val direction: EnumFacing) : IStatesEnum, IStringSerializable {
        NORTH(OrientationLevel.CENTER, EnumFacing.NORTH),
        EAST(OrientationLevel.CENTER, EnumFacing.EAST),
        SOUTH(OrientationLevel.CENTER, EnumFacing.SOUTH),
        WEST(OrientationLevel.CENTER, EnumFacing.WEST),
        UP_NORTH(OrientationLevel.UP, EnumFacing.NORTH),
        UP_EAST(OrientationLevel.UP, EnumFacing.EAST),
        UP_SOUTH(OrientationLevel.UP, EnumFacing.SOUTH),
        UP_WEST(OrientationLevel.UP, EnumFacing.WEST),
        DOWN_NORTH(OrientationLevel.DOWN, EnumFacing.NORTH),
        DOWN_EAST(OrientationLevel.DOWN, EnumFacing.EAST),
        DOWN_SOUTH(OrientationLevel.DOWN, EnumFacing.SOUTH),
        DOWN_WEST(OrientationLevel.DOWN, EnumFacing.WEST);

        override val isVisible: Boolean get() = this == NORTH

        override fun getName() = name.toLowerCase()
        override val stateName: String = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ROBOT_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ROBOT_ORIENTATION, this)
        }

        val facing
            get() = when (level) {
                OrientationLevel.UP -> EnumFacing.UP
                OrientationLevel.CENTER -> direction
                OrientationLevel.DOWN -> EnumFacing.DOWN
            }

        companion object {
            fun of(facing: EnumFacing) = when (facing) {
                EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH -> NORTH
                EnumFacing.SOUTH -> SOUTH
                EnumFacing.WEST -> WEST
                EnumFacing.EAST -> EAST
            }

            fun get(level: OrientationLevel, direction: EnumFacing) = find(level, direction)!!

            fun find(level: OrientationLevel, direction: EnumFacing): RobotOrientation? {
                return values().find { it.level == level && it.direction == direction }
            }
        }

        fun rotateY(): RobotOrientation = get(level, direction.rotateY())
        fun rotateYCCW(): RobotOrientation = get(level, direction.rotateYCCW())
    }
}