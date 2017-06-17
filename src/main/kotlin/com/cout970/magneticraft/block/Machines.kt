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
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/12.
 */
object Machines : IBlockMaker {

    val PROPERTY_CONVEYOR_ORIENTATION = PropertyEnum.create("conveyor_belt_orientation",
            ConveyorBeltOrientation::class.java)!!

    lateinit var box: BlockBase private set
    lateinit var crushingTable: BlockBase private set
    lateinit var conveyorBelt: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.ROCK
            creativeTab = CreativeTabMg
        }

        box = builder.withName("box").apply {
            factory = factoryOf(::TileBox); onActivated = BlockBuilder.openGui
        }.build()

        crushingTable = builder.withName("crushing_table").apply {
            factory = factoryOf(::TileCrushingTable)
            onActivated = { it.worldIn.getTile<TileCrushingTable>(it.pos)?.crushingModule?.onActivated(it) ?: false }
            boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.875, 1) }
            enableOcclusionOptimization = false
            translucent = true
        }.build()

        conveyorBelt = builder.withName("conveyor_belt").apply {
            factory = factoryOf(::TileConveyorBelt)
            boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.8125, 1) }
            onActivated = null
            boundingBox = null
            states = ConveyorBeltOrientation.values().toList()
            onBlockPlaced = {
                it.placer?.horizontalFacing?.toConveyorBeltOrientation()?.getBlockState(conveyorBelt) ?: it.defaultValue
            }
            enableOcclusionOptimization = false
            translucent = true
            customModels = listOf("model" to resource("models/block/mcx/conveyor_belt.mcx"))
        }.build()

        return itemBlockListOf(box, crushingTable, conveyorBelt)
    }

    enum class ConveyorBeltOrientation(override val stateName: String,
                                       override val isVisible: Boolean,
                                       val facing: EnumFacing) : IStatesEnum, IStringSerializable {
        NORTH("north", false, EnumFacing.NORTH),
        SOUTH("south", false, EnumFacing.SOUTH),
        EAST("east", false, EnumFacing.EAST),
        WEST("west", false, EnumFacing.WEST),
        INVENTORY("inventory", true, EnumFacing.UP);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_CONVEYOR_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_CONVEYOR_ORIENTATION, this)
        }

    }

    fun EnumFacing.toConveyorBeltOrientation() = when (this) {
        EnumFacing.DOWN, EnumFacing.UP -> ConveyorBeltOrientation.NORTH
        EnumFacing.NORTH -> ConveyorBeltOrientation.NORTH
        EnumFacing.SOUTH -> ConveyorBeltOrientation.SOUTH
        EnumFacing.WEST -> ConveyorBeltOrientation.WEST
        EnumFacing.EAST -> ConveyorBeltOrientation.EAST
    }
}