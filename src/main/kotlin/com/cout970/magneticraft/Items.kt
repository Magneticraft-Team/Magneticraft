package com.cout970.magneticraft

import com.cout970.magneticraft.item.ItemGuideBook
import net.minecraftforge.fml.common.registry.GameRegistry

val items = listOf(ItemGuideBook)

fun registerItems() {
    items.forEach {
        GameRegistry.register(it)
    }
}