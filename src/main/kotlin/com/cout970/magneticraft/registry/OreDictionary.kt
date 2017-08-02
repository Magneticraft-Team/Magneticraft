package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.item.Crafting
import com.cout970.magneticraft.item.Metal
import com.cout970.magneticraft.misc.inventory.stack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

/**
 * This register all the ore dictionary names
 */
fun registerOreDictionaryEntries() {

    Metal.values().forEach {
        if(!it.vanilla && !it.isComposite){
            OreDictionary.registerOre("ingot${it.name.toLowerCase().capitalize()}", it.getIngot())
            OreDictionary.registerOre("nugget${it.name.toLowerCase().capitalize()}", it.getNugget())
        }
        if(it.useful && !it.isComposite) {
            OreDictionary.registerOre("lightPlate${it.name.toLowerCase().capitalize()}", it.getLightPlate())
            OreDictionary.registerOre("heavyPlate${it.name.toLowerCase().capitalize()}", it.getHeavyPlate())
        }
        OreDictionary.registerOre("chunk${it.name.toLowerCase().capitalize()}", it.getChunk())
        OreDictionary.registerOre("rockyChunk${it.name.toLowerCase().capitalize()}", it.getRockyChunk())
    }
    OreDictionary.registerOre("dustSulfur", Crafting.crafting.stack(1, Crafting.meta["sulfur"]!!))
    OreDictionary.registerOre("oreCopper", Ores.ores.stack(1, 0))
    OreDictionary.registerOre("oreGalena", Ores.ores.stack(1, 1))
    OreDictionary.registerOre("oreCobalt", Ores.ores.stack(1, 2))
    OreDictionary.registerOre("oreTungsten", Ores.ores.stack(1, 3))
}