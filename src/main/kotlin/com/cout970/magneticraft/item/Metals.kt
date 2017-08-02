package com.cout970.magneticraft.item

import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.item.Item

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
                    .filterNot(Metal::isComposite)
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
}