package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.internal.registries.generation.OreGenerationRegistry
import com.cout970.magneticraft.api.registries.generation.OreGeneration
import com.cout970.magneticraft.block.Decoration
import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.config.OreConfig
import com.cout970.magneticraft.item.CraftingItems
import com.cout970.magneticraft.item.EnumMetal
import com.cout970.magneticraft.misc.inventory.stack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 24/06/2016.
 */

/**
 * This register all the ore dictionary names
 */
fun registerOreDictionaryEntries() {

    EnumMetal.values().forEach {
        if (!it.vanilla && !it.isComposite) {
            OreDictionary.registerOre("ingot${it.name.toLowerCase().capitalize()}", it.getIngot())
            OreDictionary.registerOre("nugget${it.name.toLowerCase().capitalize()}", it.getNugget())
        }
        if (it.useful && !it.isComposite) {
            OreDictionary.registerOre("lightPlate${it.name.toLowerCase().capitalize()}", it.getLightPlate())
            OreDictionary.registerOre("heavyPlate${it.name.toLowerCase().capitalize()}", it.getHeavyPlate())
        }
        if (!it.isComposite) {
            OreDictionary.registerOre("chunk${it.name.toLowerCase().capitalize()}", it.getChunk())
        }
        OreDictionary.registerOre("rockyChunk${it.name.toLowerCase().capitalize()}", it.getRockyChunk())
        if (it.subComponents.isEmpty()) {
            OreDictionary.registerOre("dust${it.name.toLowerCase().capitalize()}", it.getDust())
        }
    }

    //oreAluminium has two names!
    EnumMetal.ALUMINIUM.let {
        OreDictionary.registerOre("ingotAluminum", it.getIngot())
        OreDictionary.registerOre("nuggetAluminum", it.getNugget())
        OreDictionary.registerOre("dustAluminum", it.getDust())
        OreDictionary.registerOre("chunkAluminum", it.getChunk())
        OreDictionary.registerOre("rockyChunkAluminum", it.getRockyChunk())
    }

    OreDictionary.registerOre("dustSulfur", CraftingItems.crafting.stack(1, CraftingItems.Type.SULFUR.meta))
    OreDictionary.registerOre("oreCopper", Ores.ores.stack(1, 0))
    OreDictionary.registerOre("oreGalena", Ores.ores.stack(1, 1))
    OreDictionary.registerOre("oreCobalt", Ores.ores.stack(1, 2))
    OreDictionary.registerOre("oreTungsten", Ores.ores.stack(1, 3))
    OreDictionary.registerOre("orePyrite", Ores.ores.stack(1, 4))

    OreDictionary.registerOre("blockCopper", Ores.storageBlocks.stack(1, 0))
    OreDictionary.registerOre("blockGalena", Ores.storageBlocks.stack(1, 1))
    OreDictionary.registerOre("blockCobalt", Ores.storageBlocks.stack(1, 2))
    OreDictionary.registerOre("blockTungsten", Ores.storageBlocks.stack(1, 3))
    OreDictionary.registerOre("blockSulfur", Ores.storageBlocks.stack(1, 4))

    OreDictionary.registerOre("blockLimestone", Decoration.limestone.stack(1, 0))
    OreDictionary.registerOre("brickLimestone", Decoration.limestone.stack(1, 1))
    OreDictionary.registerOre("cobbleLimestone", Decoration.limestone.stack(1, 2))

    OreDictionary.registerOre("blockBurnLimestone", Decoration.burnLimestone.stack(1, 0))
    OreDictionary.registerOre("brickBurnLimestone", Decoration.burnLimestone.stack(1, 1))
    OreDictionary.registerOre("cobbleBurnLimestone", Decoration.burnLimestone.stack(1, 2))

    OreDictionary.registerOre("blockTileLimestone", Decoration.tileLimestone.stack(1, 0))
    OreDictionary.registerOre("blockTileLimestone", Decoration.tileLimestone.stack(1, 1))
}

fun registerOreGenerations() {
    OreGenerationRegistry.registerOreGeneration(Config.copperOre.toOG("oreCopper"))
    OreGenerationRegistry.registerOreGeneration(Config.leadOre.toOG("oreGalena"))
    OreGenerationRegistry.registerOreGeneration(Config.pyriteOre.toOG("orePyrite"))
    OreGenerationRegistry.registerOreGeneration(Config.tungstenOre.toOG("oreTungsten"))
}

fun OreConfig.toOG(name: String): OreGeneration {
    return OreGeneration(name, active, chunkAmount, veinAmount, maxLevel, minLevel)
}