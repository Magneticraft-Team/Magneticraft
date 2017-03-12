package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.decoration.BlockFiberboard
import com.cout970.magneticraft.item.*
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

/**
 * This register all the ore dictionary names
 */
fun registerOreDictionaryEntries() {

    OreDictionary.registerOre("gemLapis", ItemStack(ItemCrushedLapis))
    OreDictionary.registerOre("gemLapis", ItemStack(ItemPebblesLapis))
    OreDictionary.registerOre("plankWood", ItemStack(BlockFiberboard))

    //todo
    /*for ((key, value) in BlockOre.variants) {
        val parts = value.split("=")
        val name = parts[0] + parts[1].toLowerCase().capitalize()
        if (key == 2) {
            OreDictionary.registerOre("${name}_Mgc", ItemStack(BlockOre, 1, key))
        } else {
            OreDictionary.registerOre(name, ItemStack(BlockOre, 1, key))
        }
    }*/

    for ((key, value) in ItemIngot.variants) {
        if (key == 4) {
            OreDictionary.registerOre("ingot${value.replace("ore=", "").capitalize()}_Mgc", ItemStack(ItemIngot, 1, key))
        } else {
            OreDictionary.registerOre("ingot${value.replace("ore=", "").capitalize()}", ItemStack(ItemIngot, 1, key))
        }
    }

    for ((key, value) in ItemCrushedOre.variants) {
        if (key == 4) {
            OreDictionary.registerOre("crushedOre${value.replace("ore=", "").capitalize()}_Mgc", ItemStack(ItemCrushedOre, 1, key))
        } else {
            OreDictionary.registerOre("crushedOre${value.replace("ore=", "").capitalize()}", ItemStack(ItemCrushedOre, 1, key))
        }
    }

    for ((key, value) in ItemPebbles.variants) {
        if (key == 4) {
            OreDictionary.registerOre("pebbles${value.replace("ore=", "").capitalize()}_Mgc", ItemStack(ItemPebbles, 1, key))
        } else {
            OreDictionary.registerOre("pebbles${value.replace("ore=", "").capitalize()}", ItemStack(ItemPebbles, 1, key))
        }
    }

    for ((key, value) in ItemHeavyPlate.variants) {
        if (key == 4) {
            OreDictionary.registerOre("heavyPlate${value.replace("ore=", "").capitalize()}_Mgc", ItemStack(ItemHeavyPlate, 1, key))
        } else {
            OreDictionary.registerOre("heavyPlate${value.replace("ore=", "").capitalize()}", ItemStack(ItemHeavyPlate, 1, key))
        }
    }

    for ((key, value) in ItemLightPlate.variants) {
        if (key == 4) {
            OreDictionary.registerOre("lightPlate${value.replace("ore=", "").capitalize()}_Mgc", ItemStack(ItemLightPlate, 1, key))
        } else {
            OreDictionary.registerOre("lightPlate${value.replace("ore=", "").capitalize()}", ItemStack(ItemLightPlate, 1, key))
        }
    }

    for ((key, value) in ItemNugget.variants) {
        if (key == 4) {
            OreDictionary.registerOre("nugget${value.replace("ore=", "").capitalize()}_Mgc", ItemStack(ItemNugget, 1, key))
        } else {
            OreDictionary.registerOre("nugget${value.replace("ore=", "").capitalize()}", ItemStack(ItemNugget, 1, key))
        }
    }
}


/**
 * Return one of the items registered with 'name' in the ore dictionary or null if there is none with that name
 */
@Suppress("unused")
fun getItemFromDict(name: String): ItemStack? {
    return OreDictionary.getOres(name).firstOrNull()?.copy()
}

