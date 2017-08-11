package com.cout970.magneticraft.api.internal.registries.generation

import com.cout970.magneticraft.api.registries.generation.IOreGenerationRegistry
import com.cout970.magneticraft.api.registries.generation.OreGeneration

/**
 * Created by cout970 on 2017/07/12.
 */
object OreGenerationRegistry : IOreGenerationRegistry {

    private val registered = mutableMapOf<String, OreGeneration>()

    override fun isRegistered(oreDictName: String): Boolean = oreDictName in registered

    override fun getOreGeneration(oreDictName: String): OreGeneration? = registered[oreDictName]

    override fun getRegisteredOres(): MutableMap<String, OreGeneration> = registered.toMutableMap()

    override fun registerOreGeneration(gen: OreGeneration): Boolean {
        val override = gen.oreDictName in registered
        registered += gen.oreDictName to gen
        return override
    }
}