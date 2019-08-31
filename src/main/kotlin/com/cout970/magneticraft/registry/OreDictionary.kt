package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.internal.registries.generation.OreGenerationRegistry
import com.cout970.magneticraft.api.registries.generation.OreGeneration
import com.cout970.magneticraft.features.items.CraftingItems
import com.cout970.magneticraft.features.items.EnumMetal
import com.cout970.magneticraft.features.items.ToolItems
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.config.OreConfig
import net.minecraftforge.oredict.OreDictionary
import com.cout970.magneticraft.features.decoration.Blocks as DecorationBlocks
import com.cout970.magneticraft.features.ores.Blocks as OreBlocks

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
    OreDictionary.registerOre("oreCopper", OreBlocks.ores.stack(1, 0))
    OreDictionary.registerOre("oreGalena", OreBlocks.ores.stack(1, 1))
    OreDictionary.registerOre("oreLead", OreBlocks.ores.stack(1, 1))
    OreDictionary.registerOre("oreSilver", OreBlocks.ores.stack(1, 1))

    OreDictionary.registerOre("oreCobalt", OreBlocks.ores.stack(1, 2))
    OreDictionary.registerOre("oreTungsten", OreBlocks.ores.stack(1, 3))
    OreDictionary.registerOre("orePyrite", OreBlocks.ores.stack(1, 4))
    OreDictionary.registerOre("oreSulfur", OreBlocks.ores.stack(1, 4))

    OreDictionary.registerOre("blockCopper", OreBlocks.storageBlocks.stack(1, 0))
    OreDictionary.registerOre("blockLead", OreBlocks.storageBlocks.stack(1, 1))
    OreDictionary.registerOre("blockCobalt", OreBlocks.storageBlocks.stack(1, 2))
    OreDictionary.registerOre("blockTungsten", OreBlocks.storageBlocks.stack(1, 3))
    OreDictionary.registerOre("blockSulfur", OreBlocks.storageBlocks.stack(1, 4))

    OreDictionary.registerOre("stoneLimestonePolished", DecorationBlocks.limestone.stack(1, 0))
    OreDictionary.registerOre("brickLimestone", DecorationBlocks.limestone.stack(1, 1))
    OreDictionary.registerOre("stoneLimestone", DecorationBlocks.limestone.stack(1, 2))

    OreDictionary.registerOre("stoneBurnLimestonePolished", DecorationBlocks.burnLimestone.stack(1, 0))
    OreDictionary.registerOre("brickBurnLimestone", DecorationBlocks.burnLimestone.stack(1, 1))
    OreDictionary.registerOre("stoneBurnLimestone", DecorationBlocks.burnLimestone.stack(1, 2))

    OreDictionary.registerOre("stoneTileLimestone", DecorationBlocks.tileLimestone.stack(1, 0))
    OreDictionary.registerOre("stoneTileLimestone", DecorationBlocks.tileLimestone.stack(1, 1))
    OreDictionary.registerOre("magneticraftHammer", ToolItems.stoneHammer)
    OreDictionary.registerOre("magneticraftHammer", ToolItems.ironHammer)
    OreDictionary.registerOre("magneticraftHammer", ToolItems.steelHammer)
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