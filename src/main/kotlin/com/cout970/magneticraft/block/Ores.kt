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
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/11.
 */
object Ores : IBlockMaker {

    val PROPERTY_ORE_TYPE = PropertyEnum.create("ore_type", OreType::class.java)!!
    val PROPERTY_OIL_AMOUNT = PropertyEnum.create("oil_amount", OilAmount::class.java)!!

    lateinit var ores: BlockBase private set
    lateinit var oilSource: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.ROCK
            creativeTab = CreativeTabMg
        }

        ores = builder.withName("ores").copy {
            states = OreType.values().toList()
        }.build()

        oilSource = builder.withName("oil_source").copy {
            states = OilAmount.values().toList()
        }.build()

        ores.setHarvestLevel("pickaxe", 1, OreType.COPPER.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 1, OreType.LEAD.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 2, OreType.COBALT.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 2, OreType.TUNGSTEN.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 1, OreType.PYRITE.getBlockState(ores))

        return itemBlockListOf(ores, oilSource)
    }

    enum class OilAmount(override val stateName: String,
                         override val isVisible: Boolean,
                         val amount: Float) : IStatesEnum, IStringSerializable {

        FULL_100("full_100", true, 1f),
        FULL_90("full_90", false, 0.9f),
        FULL_80("full_80", false, 0.8f),
        FULL_70("full_70", false, 0.7f),
        FULL_60("full_60", false, 0.6f),
        FULL_50("full_50", false, 0.5f),
        FULL_40("full_40", false, 0.4f),
        FULL_30("full_30", false, 0.3f),
        FULL_20("full_20", false, 0.2f),
        FULL_10("full_10", false, 0.1f),
        EMPTY("empty", true, 0f);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_OIL_AMOUNT)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_OIL_AMOUNT, this)
        }
    }

    enum class OreType(override val stateName: String,
                       override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        COPPER("copper", true),
        LEAD("lead", true),
        COBALT("cobalt", true),
        TUNGSTEN("tungsten", true),
        PYRITE("pyrite", true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ORE_TYPE)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ORE_TYPE, this)
        }
    }
}