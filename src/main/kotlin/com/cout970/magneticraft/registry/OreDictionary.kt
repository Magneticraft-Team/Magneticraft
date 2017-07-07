package com.cout970.magneticraft.registry

import com.cout970.magneticraft.item.Metals
import com.cout970.magneticraft.item.core.ItemBase
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

/**
 * This register all the ore dictionary names
 */
fun registerOreDictionaryEntries() {

    register(Metals.ingots, "ingot")
    register(Metals.nuggets, "nugget")
    register(Metals.lightPlates, "lightPlate")
    register(Metals.heavyPlates, "heavyPlate")
    register(Metals.chunks, "chunk")
}

private fun register(item: ItemBase, prefix: String){
    item.variants.forEach { meta, name ->
        OreDictionary.registerOre("$prefix${name.capitalize()}", ItemStack(item, 1, meta))
    }
}