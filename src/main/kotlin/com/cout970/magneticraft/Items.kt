package com.cout970.magneticraft

import com.cout970.magneticraft.item.ItemCrushedOre
import com.cout970.magneticraft.item.ItemGuideBook
import com.cout970.magneticraft.item.ItemHandSieve
import com.cout970.magneticraft.item.ItemPebbles
import com.cout970.magneticraft.item.hammers.ItemIronHammer
import com.cout970.magneticraft.item.hammers.ItemStoneHammer
import net.minecraftforge.fml.common.registry.GameRegistry

val items = listOf(
        ItemGuideBook,
        ItemIronHammer,
        ItemStoneHammer,
        ItemCrushedOre,
        ItemHandSieve,
        ItemPebbles
)

fun registerItems() {
    items.forEach {
        GameRegistry.register(it)
    }
}