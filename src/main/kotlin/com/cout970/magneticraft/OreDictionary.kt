package com.cout970.magneticraft

import com.cout970.magneticraft.item.ItemCrushedOre
import com.cout970.magneticraft.item.ItemIngots
import com.cout970.magneticraft.item.ItemPebbles
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

fun registerOreDictionaryEntries() {

    for (i in ItemIngots.INGOTS) {
        OreDictionary.registerOre("ore${format(i.value)}", ItemStack(ItemIngots, 1, i.key))
    }

    for (i in ItemCrushedOre.CRUSHED_ORES) {
        OreDictionary.registerOre("crushedore${format(i.value)}", ItemStack(ItemCrushedOre, 1, i.key))
    }

    for (i in ItemPebbles.PEBBLES) {
        OreDictionary.registerOre("pebbles${format(i.value)}", ItemStack(ItemPebbles, 1, i.key))
    }
}

private fun format(string: String) = string[0].toUpperCase() + string.substring(1).toLowerCase()