package com.cout970.magneticraft

import com.cout970.magneticraft.item.ItemGuideBook
import com.cout970.magneticraft.item.hammers.ItemIronHammer
import net.minecraftforge.fml.common.registry.GameRegistry

val items = listOf(
    ItemGuideBook,
    ItemIronHammer
)

fun registerItems() {
    items.forEach {
        GameRegistry.register(it)
    }
}