package com.cout970.magneticraft.registry

import com.cout970.magneticraft.item.ItemCrushedOre
import com.cout970.magneticraft.item.ItemIngots
import com.cout970.magneticraft.item.ItemPebbles
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

fun registerOreDictionaryEntries() {

    for (i in ItemIngots.variants) {
        OreDictionary.registerOre("ore${i.value.capitalize()}", ItemStack(ItemIngots, 1, i.key))
    }

    for (i in ItemCrushedOre.variants) {
        OreDictionary.registerOre("crushedore${i.value.capitalize()}", ItemStack(ItemCrushedOre, 1, i.key))
    }

    for (i in ItemPebbles.variants) {
        OreDictionary.registerOre("pebbles${i.value.capitalize()}", ItemStack(ItemPebbles, 1, i.key))
    }
}