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
    /*for (a in BlockOre.variants) {
        val parts = value.split("=")
        val name = parts[0] + parts[1].toLowerCase().capitalize()
        if (index == 2) {
            OreDictionary.registerOre("${name}_Mgc", ItemStack(BlockOre, 1, index))
        } else {
            OreDictionary.registerOre(name, ItemStack(BlockOre, 1, index))
        }
    }*/

    //todo all the stuff i commented out
    for (a in ItemIngot.variants.indices) {
        val index = a
        val a = ItemIngot.variants[a]
        if (index == 4) {
            OreDictionary.registerOre("ingot${a.capitalize()}_Mgc", ItemStack(ItemIngot, 1, index))
        } else {
            OreDictionary.registerOre("ingot${a.capitalize()}", ItemStack(ItemIngot, 1, index))
        }
    }

    for (a in ItemCrushedOre.variants.indices) {
        val index = a
        val a = ItemCrushedOre.variants[index]
        if (index == 4) {
          OreDictionary.registerOre("crushedOre${a.capitalize()}_Mgc", ItemStack(ItemCrushedOre, 1, index))
       } else {
            OreDictionary.registerOre("crushedOre${a.capitalize()}", ItemStack(ItemCrushedOre, 1, index))
        }
    }

    for (a in ItemPebbles.variants.indices) {
        val index = a
        val a = ItemPebbles.variants[index]
        if (index == 4) {
            OreDictionary.registerOre("pebbles${a.capitalize()}_Mgc", ItemStack(ItemPebbles, 1, index))
        } else {
            OreDictionary.registerOre("pebbles${a.capitalize()}", ItemStack(ItemPebbles, 1, index))
        }
    }

    for (a in ItemHeavyPlate.variants.indices) {
        val index = a
        val a = ItemHeavyPlate.variants[index]
        if (index == 4) {
            OreDictionary.registerOre("heavyPlate${a.capitalize()}_Mgc", ItemStack(ItemHeavyPlate, 1, index))
        } else {
            OreDictionary.registerOre("heavyPlate${a.capitalize()}", ItemStack(ItemHeavyPlate, 1, index))
        }
    }

    for (a in ItemLightPlate.variants.indices) {
        val index = a
        val a = ItemLightPlate.variants[index]
        if (index == 4) {
            OreDictionary.registerOre("lightPlate${a.capitalize()}_Mgc", ItemStack(ItemLightPlate, 1, index))
        } else {
            OreDictionary.registerOre("lightPlate${a.capitalize()}", ItemStack(ItemLightPlate, 1, index))
        }
    }

    for (a in ItemNugget.variants.indices) {
        val index = a
        val a = ItemNugget.variants[index]
        if (index == 4) {
            OreDictionary.registerOre("nugget${a.capitalize()}_Mgc", ItemStack(ItemNugget, 1, index))
        } else {
            OreDictionary.registerOre("nugget${a.capitalize()}", ItemStack(ItemNugget, 1, index))
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

