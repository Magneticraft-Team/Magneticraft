package com.cout970.magneticraft.registry

import com.cout970.magneticraft.item.ItemCrushedOre
import com.cout970.magneticraft.item.ItemIngot
import com.cout970.magneticraft.item.ItemPebbles
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

fun registerOreDictionaryEntries() {

    for ((key, value) in ItemIngot.variants) {
        OreDictionary.registerOre("ore${value.capitalize()}", ItemStack(ItemIngot, 1, key))
    }

    for ((key, value) in ItemCrushedOre.variants) {
        OreDictionary.registerOre("crushedore${value.capitalize()}", ItemStack(ItemCrushedOre, 1, key))
    }

    for ((key, value) in ItemPebbles.variants) {
        OreDictionary.registerOre("pebbles${value.capitalize()}", ItemStack(ItemPebbles, 1, key))
    }
}