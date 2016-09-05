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
        OreDictionary.registerOre("ingot${value.replace("ore=", "").capitalize()}", ItemStack(ItemIngot, 1, key))
    }

    for ((key, value) in ItemCrushedOre.variants) {
        OreDictionary.registerOre("crushedore${value.replace("ore=", "").capitalize()}", ItemStack(ItemCrushedOre, 1, key))
    }

    for ((key, value) in ItemPebbles.variants) {
        OreDictionary.registerOre("pebbles${value.replace("ore=", "").capitalize()}", ItemStack(ItemPebbles, 1, key))
    }

    for ((key, value) in ItemIngot.variants) {
        OreDictionary.registerOre("heavyPlate${value.replace("ore=", "").capitalize()}", ItemStack(ItemIngot, 1, key))
    }
}

fun getItemFromDict(name:String): ItemStack? {
    return OreDictionary.getOres(name).firstOrNull()?.copy()
}

