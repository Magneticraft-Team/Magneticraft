package com.cout970.magneticraft.item

import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.item.Item

/**
 * Created by cout970 on 2017/07/22.
 */
object Crafting : IItemMaker {

    lateinit var crafting: ItemBase private set
    val meta = mapOf(
            "sulfur" to 0,
            "alternator" to 1,
            "motor" to 2,
            "coil" to 3,
            "magnet" to 4,
            "mesh" to 5,
            "string_fabric" to 6
    )


    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        crafting = builder.withName("crafting").apply {
            variants = meta.map { it.value to it.key }.toMap()
        }.build()


        return listOf(crafting)
    }
}