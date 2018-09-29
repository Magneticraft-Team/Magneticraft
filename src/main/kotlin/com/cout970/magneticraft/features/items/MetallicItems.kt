package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import net.minecraft.item.Item

/**
 * Created by cout970 on 2017/06/11.
 */
object MetallicItems : IItemMaker {

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
            variants = EnumMetal.values()
                .filterNot(EnumMetal::isComposite)
                .filterNot(EnumMetal::vanilla)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        lightPlates = builder.withName("light_plates").apply {
            variants = EnumMetal.values()
                .filter(EnumMetal::useful)
                .filterNot(EnumMetal::isComposite)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        heavyPlates = builder.withName("heavy_plates").apply {
            variants = EnumMetal.values()
                .filter(EnumMetal::useful)
                .filterNot(EnumMetal::isComposite)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        nuggets = builder.withName("nuggets").apply {
            variants = EnumMetal.values()
                .filterNot(EnumMetal::isComposite)
                .filterNot(EnumMetal::vanilla)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        chunks = builder.withName("chunks").apply {
            variants = EnumMetal.values()
                .filter(EnumMetal::isOre)
                .filterNot(EnumMetal::isComposite)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        dusts = builder.withName("dusts").apply {
            variants = EnumMetal.values()
                .filterNot(EnumMetal::isComposite)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        rockyChunks = builder.withName("rocky_chunks").apply {
            variants = EnumMetal.values()
                .filter(EnumMetal::isOre)
                .map { it.ordinal to it.name.toLowerCase() }
                .toMap()
        }.build()

        return listOf(ingots, lightPlates, heavyPlates, nuggets, chunks, dusts, rockyChunks)
    }
}