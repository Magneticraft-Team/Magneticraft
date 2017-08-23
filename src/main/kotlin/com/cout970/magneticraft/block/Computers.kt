package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.*
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileComputer
import com.cout970.magneticraft.tileentity.TileMiningRobot
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/07/07.
 */
object Computers : IBlockMaker {

    val PROPERTY_ROBOT_ORIENTATION = PropertyEnum.create("robot_orientation", RobotOrientation::class.java)!!


    lateinit var computer: BlockBase private set
    lateinit var miningRobot: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
            alwaysDropDefault = true
        }

        computer = builder.withName("computer").copy {
            factory = factoryOf(::TileComputer)
            states = CommonMethods.Orientation.values().toList()
            hasCustomModel = true
            generateDefaultItemModel = false
            customModels = listOf(
                    "model" to resource("models/block/mcx/computer.mcx"),
                    "inventory" to resource("models/block/mcx/computer.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
            onActivated = CommonMethods::delegateToModule
        }.build()

        miningRobot = builder.withName("mining_robot").copy {
            factory = factoryOf(::TileMiningRobot)
            states = RobotOrientation.values().toList()
            hasCustomModel = true
            generateDefaultItemModel = false
            customModels = listOf(
                    "model" to resource("models/block/mcx/mining_robot.mcx"),
                    "inventory" to resource("models/block/mcx/mining_robot.mcx")
            )
            //methods
            onBlockPlaced = Computers::placeWithRobotOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
            onActivated = CommonMethods::delegateToModule
        }.build()

        return itemBlockListOf(computer, miningRobot)
    }

    fun placeWithRobotOrientation(it: OnBlockPlacedArgs): IBlockState {
        return it.defaultValue.withProperty(PROPERTY_ROBOT_ORIENTATION,
                RobotOrientation.of(it.placer?.horizontalFacing ?: EnumFacing.NORTH))
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
                Computers.OrientationLevel.UP -> EnumFacing.UP
                Computers.OrientationLevel.CENTER -> direction
                Computers.OrientationLevel.DOWN -> EnumFacing.DOWN
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