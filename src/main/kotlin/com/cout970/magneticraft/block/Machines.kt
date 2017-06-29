package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.block.core.IStatesEnum
import com.cout970.magneticraft.item.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileInserter
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.vec3Of
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
 * Created by cout970 on 2017/06/12.
 */
object Machines : IBlockMaker {

    val PROPERTY_ORIENTATION = PropertyEnum.create("orientation", Orientation::class.java)!!

    lateinit var box: BlockBase private set
    lateinit var crushingTable: BlockBase private set
    lateinit var conveyorBelt: BlockBase private set
    lateinit var inserter: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        box = builder.withName("box").copy {
            factory = factoryOf(::TileBox); onActivated = BlockBuilder.openGui
            material = Material.WOOD
        }.build()

        crushingTable = builder.withName("crushing_table").copy {
            factory = factoryOf(::TileCrushingTable)
            material = Material.ROCK
            customModels = listOf("normal" to resource("models/block/mcx/crushing_table.mcx"))
            enableOcclusionOptimization = false
            translucent = true
            //methods
            boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.875, 1) }
            onActivated = { it.worldIn.getTile<TileCrushingTable>(it.pos)?.crushingModule?.onActivated(it) ?: false }
        }.build()

        conveyorBelt = builder.withName("conveyor_belt").copy {
            states = Orientation.values().toList()
            factory = factoryOf(::TileConveyorBelt)
            customModels = listOf(
                    "model" to resource("models/block/mcx/conveyor_belt.mcx"),
                    "inventory" to resource("models/block/mcx/conveyor_belt.mcx")
            )
            enableOcclusionOptimization = false
            translucent = true
            //methods
            boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.8125, 1) }
            onBlockPlaced = {
                it.placer?.horizontalFacing?.toOrientation()?.getBlockState(conveyorBelt) ?: it.defaultValue
            }
            pickBlock = { ItemStack(it.default.item, 1, Orientation.NORTH.ordinal) }
            overrideItemModel = false
            onActivated = { it.worldIn.getTile<TileConveyorBelt>(it.pos)?.conveyorModule?.onClick(it) ?: false }
        }.build()

        inserter = builder.withName("inserter").copy {
            states = Orientation.values().toList()
            factory = factoryOf(::TileInserter)
            customModels = listOf(
                    "model" to resource("models/block/mcx/inserter.mcx"),
                    "inventory" to resource("models/block/mcx/inserter.mcx")
            )
            enableOcclusionOptimization = false
            translucent = true
            //methods
            onBlockPlaced = {
                it.placer?.horizontalFacing?.toOrientation()?.getBlockState(inserter) ?: it.defaultValue
            }
            pickBlock = { ItemStack(it.default.item, 1, Orientation.NORTH.ordinal) }
            overrideItemModel = false
            onActivated = BlockBuilder.openGui
        }.build()

        return itemBlockListOf(box, crushingTable, conveyorBelt, inserter)
    }

    enum class Orientation(override val stateName: String,
                           override val isVisible: Boolean,
                           val facing: EnumFacing) : IStatesEnum, IStringSerializable {
        NORTH("north", true, EnumFacing.NORTH),
        SOUTH("south", false, EnumFacing.SOUTH),
        EAST("east", false, EnumFacing.EAST),
        WEST("west", false, EnumFacing.WEST);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ORIENTATION, this)
        }
    }

    fun EnumFacing.toOrientation() = when (this) {
        EnumFacing.DOWN, EnumFacing.UP -> Orientation.NORTH
        EnumFacing.NORTH -> Orientation.NORTH
        EnumFacing.SOUTH -> Orientation.SOUTH
        EnumFacing.WEST -> Orientation.WEST
        EnumFacing.EAST -> Orientation.EAST
    }
}
