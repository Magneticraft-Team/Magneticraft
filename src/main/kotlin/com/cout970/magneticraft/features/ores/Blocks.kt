package com.cout970.magneticraft.features.ores

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/11.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    val PROPERTY_ORE_TYPE: PropertyEnum<OreType> =
        PropertyEnum.create("ore_type", OreType::class.java)

    val PROPERTY_OIL_AMOUNT: PropertyEnum<OilAmount> =
        PropertyEnum.create("oil_amount", OilAmount::class.java)

    lateinit var ores: BlockBase private set
    lateinit var oilSource: BlockBase private set
    lateinit var storageBlocks: BlockBase private set

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
            pickBlock = CommonMethods::pickDefaultBlock
            onDrop = { listOf(ItemStack.EMPTY) }
        }.build()

        storageBlocks = builder.withName("storage_blocks").copy {
            states = OreType.values().toList()
        }.build()

        ores.setHarvestLevel("pickaxe", 1, OreType.COPPER.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 1, OreType.LEAD.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 2, OreType.COBALT.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 2, OreType.TUNGSTEN.getBlockState(ores))
        ores.setHarvestLevel("pickaxe", 1, OreType.PYRITE.getBlockState(ores))

        return itemBlockListOf(ores, oilSource, storageBlocks)
    }

    enum class OilAmount(override val stateName: String,
                         override val isVisible: Boolean,
                         val amount: Int) : IStatesEnum, IStringSerializable {

        FULL_100("full_100", true, 10),
        FULL_90("full_90", false, 9),
        FULL_80("full_80", false, 8),
        FULL_70("full_70", false, 7),
        FULL_60("full_60", false, 6),
        FULL_50("full_50", false, 5),
        FULL_40("full_40", false, 4),
        FULL_30("full_30", false, 3),
        FULL_20("full_20", false, 2),
        FULL_10("full_10", false, 1),
        EMPTY("empty", true, 0);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_OIL_AMOUNT)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_OIL_AMOUNT, this)
        }

        companion object {
            fun fromAmount(amount: Int) = values().find { it.amount == amount }
        }
    }

    enum class OreType(override val stateName: String,
                       override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        COPPER("copper", true),
        LEAD("lead", true),
        COBALT("cobalt", true),
        TUNGSTEN("tungsten", true),
        PYRITE("pyrite", true);

        fun stack(amount: Int = 1) = ItemStack(ores, amount, ordinal)

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_ORE_TYPE)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_ORE_TYPE, this)
        }
    }
}