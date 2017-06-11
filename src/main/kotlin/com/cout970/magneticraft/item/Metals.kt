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

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        ingots = builder.withName("ingots").apply {
            variants = mapOf(0 to "copper", 1 to "lead", 2 to "cobalt", 3 to "tungsten")
        }.build()
        lightPlates = builder.withName("light_plates").apply {
            variants = mapOf(0 to "iron", 1 to "gold", 2 to "copper", 3 to "lead", 4 to "cobalt", 5 to "tungsten")
        }.build()
        heavyPlates = builder.withName("heavy_plates").apply {
            variants = mapOf(0 to "iron", 1 to "gold", 2 to "copper", 3 to "lead", 4 to "cobalt", 5 to "tungsten")
        }.build()
        nuggets = builder.withName("nuggets").apply {
            variants = mapOf(0 to "copper", 1 to "lead", 2 to "cobalt", 3 to "tungsten")
        }.build()

        return listOf(ingots, lightPlates, heavyPlates, nuggets)
    }
}