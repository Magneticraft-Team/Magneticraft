package com.cout970.magneticraft.registry

import com.cout970.magneticraft.util.resource
import net.minecraft.util.SoundEvent
import net.minecraftforge.registries.IForgeRegistry

//Map with all the sounds in the mod
// TODO replace with enum
val sounds = listOf(
        "crushing_hit",
        "crushing_final",
        "water_flow",
        "water_flow_end"
).associate {
    it to SoundEvent(resource(it))
}

/**
 * Called by ClientProxy to register all the sounds
 */
fun registerSounds(registry: IForgeRegistry<SoundEvent>) {
    sounds.values.forEach {
        it.registryName = it.soundName
        registry.register(it)
    }
}