package com.cout970.magneticraft.item

import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.inventory.withSize
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 2017/06/11.
 */
object Metals : IItemMaker {

    lateinit var ingots: ItemBase private set
    lateinit var lightPlates: ItemBase private set
    lateinit var heavyPlates: ItemBase private set
    lateinit var nuggets: ItemBase private set
    lateinit var chunks: ItemBase private set
    lateinit var rockyChunks: ItemBase private set
    lateinit var dusts: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        ingots = builder.withName("ingots").apply {
            variants = Metal.values()
                    .filterNot(Metal::isComposite)
                    .filterNot(Metal::vanilla)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        lightPlates = builder.withName("light_plates").apply {
            variants = Metal.values()
                    .filter(Metal::useful)
                    .filterNot(Metal::isComposite)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        heavyPlates = builder.withName("heavy_plates").apply {
            variants = Metal.values()
                    .filter(Metal::useful)
                    .filterNot(Metal::isComposite)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        nuggets = builder.withName("nuggets").apply {
            variants = Metal.values()
                    .filterNot(Metal::isComposite)
                    .filterNot(Metal::vanilla)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        chunks = builder.withName("chunks").apply {
            variants = Metal.values()
                    .filter(Metal::isOre)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        dusts = builder.withName("dusts").apply {
            variants = Metal.values()
                    .filterNot(Metal::isComposite)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        rockyChunks = builder.withName("rocky_chunks").apply {
            variants = Metal.values()
                    .filter(Metal::isOre)
                    .map { it.ordinal to it.name.toLowerCase() }
                    .toMap()
        }.build()

        return listOf(ingots, lightPlates, heavyPlates, nuggets, chunks, dusts, rockyChunks)
    }

    enum class Metal(val vanilla: Boolean, val isOre: Boolean = true, val isComposite: Boolean = false,
                     val useful: Boolean = false) {
        IRON(true, useful = true),
        GOLD(true, useful = true),
        COPPER(false, useful = true),
        LEAD(false, useful = true),
        COBALT(false),
        TUNGSTEN(false, useful = true),
        STEEL(false, isOre = false, useful = true),
        ALUMINIUM(false),
        GALENA(false, isComposite = true),
        MITHRIL(false),
        NICKEL(false),
        OSMIUM(false),
        SILVER(false),
        TIN(false),
        ZINC(false);

        fun getOres(): List<ItemStack> {
            if (vanilla) {
                if (vanilla) return when (this) {
                    IRON -> listOf(Blocks.IRON_ORE.stack(1))
                    else -> listOf(Blocks.GOLD_ORE.stack(1))
                }
            }
            if (this == ALUMINIUM) {
                return (OreDictionary.getOres("oreAluminium") + OreDictionary.getOres("oreAluminum"))
                        .map { it.withSize(1) }
            }
            return OreDictionary.getOres("ore" + name.toLowerCase().capitalize()).map {
                it.withSize(1)
            }
        }

        fun getIngot(): ItemStack {
            if (vanilla) return when (this) {
                IRON -> Items.IRON_INGOT.stack(1)
                else -> Items.GOLD_INGOT.stack(1)
            }
            return ingots.stack(1, ordinal)
        }

        fun getNugget(): ItemStack {
            if (vanilla) return when (this) {
                IRON -> Items.IRON_NUGGET.stack(1)
                else -> Items.GOLD_NUGGET.stack(1)
            }
            return nuggets.stack(1, ordinal)
        }

        fun getDust(): ItemStack {
            return dusts.stack(1, ordinal)
        }

        fun getLightPlate(): ItemStack {
            return lightPlates.stack(1, ordinal)
        }

        fun getHeavyPlate(): ItemStack {
            return heavyPlates.stack(1, ordinal)
        }

        fun getChunk(): ItemStack {
            return chunks.stack(1, ordinal)
        }

        fun getRockyChunk(): ItemStack {
            return rockyChunks.stack(1, ordinal)
        }
    }
}