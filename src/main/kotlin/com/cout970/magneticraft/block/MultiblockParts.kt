package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.block.core.IStatesEnum
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/13.
 */
object MultiblockParts : IBlockMaker {

    val PROPERTY_PART_TYPE = PropertyEnum.create("part_type", PartType::class.java)!!
    val PROPERTY_COLUMN_AXIS = PropertyEnum.create("column_axis", ColumnOrientation::class.java)!!

    lateinit var parts: BlockBase private set
    lateinit var column: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        parts = builder.withName("multiblock_parts").copy {
            states = PartType.values().toList()
        }.build()
        column = builder.withName("multiblock_column").copy {
            states = ColumnOrientation.values().toList()
            alwaysDropDefault = true
            onBlockPlaced = { it.defaultValue.withProperty(PROPERTY_COLUMN_AXIS, it.facing.axis.toColumnAxis())}
        }.build()

        return itemBlockListOf(parts, column)
    }

    enum class PartType(override val stateName: String,
                       override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        BASE("base", true),
        ELECTRIC("electric", true),
        GRATE("grate", true),
        STRIPED("striped", true),
        COPPER_COIL("copper_coil", true),
        CORRUGATED_IRON("corrugated_iron", true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_PART_TYPE)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_PART_TYPE, this)
        }
    }

    enum class ColumnOrientation(override val stateName: String,
                       override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        AXIS_Y("axis_y", true),
        AXIS_X("axis_x", false),
        AXIS_Z("axis_z", false);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_COLUMN_AXIS)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_COLUMN_AXIS, this)
        }
    }

    fun EnumFacing.Axis.toColumnAxis(): MultiblockParts.ColumnOrientation = when(this){
        EnumFacing.Axis.X -> ColumnOrientation.AXIS_X
        EnumFacing.Axis.Y -> ColumnOrientation.AXIS_Y
        EnumFacing.Axis.Z -> ColumnOrientation.AXIS_Z
    }
}
