package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.block.core.IStatesEnum
import com.cout970.magneticraft.item.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/08.
 */

object Decoration : IBlockMaker {

    val PROPERTY_LIMESTONE_KIND = PropertyEnum.create("limestone_kind", LimestoneKind::class.java)!!
    val PROPERTY_INVERTED = PropertyEnum.create("inverted", TileInverted::class.java)!!

    lateinit var limestone: BlockBase private set
    lateinit var burnLimestone: BlockBase private set
    lateinit var tileLimestone: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.ROCK
            creativeTab = CreativeTabMg
            states = LimestoneKind.values().toList()
        }

        limestone = builder.withName("limestone").build()
        burnLimestone = builder.withName("burnt_limestone").build()
        tileLimestone = builder.withName("tile_limestone").apply { states = TileInverted.values().toList() }.build()

        return itemBlockListOf(limestone, burnLimestone, tileLimestone)
    }

    enum class LimestoneKind(override val stateName: String,
                             override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        NORMAL("normal", true),
        BRICK("brick", true),
        COBBLE("cobble", true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_LIMESTONE_KIND)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_LIMESTONE_KIND, this)
        }
    }

    enum class TileInverted(override val stateName: String,
                            override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        NORMAL("normal", true),
        INVERTED("inverted", true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_INVERTED)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_INVERTED, this)
        }
    }
}