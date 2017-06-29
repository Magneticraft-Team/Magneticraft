package com.cout970.magneticraft.registry

import com.cout970.magneticraft.util.resource
import net.minecraft.util.SoundEvent
import net.minecraftforge.fml.common.registry.GameRegistry

//MAp with all the sounds in the mod
val sounds = listOf(
    "crushing_hit",
    "crushing_final"
).associate {
    it to SoundEvent(resource(it))
}

/**
 * Called by ClientProxy to register all the sounds
 */
fun registerSounds() {
    val soundRegistry = GameRegistry.findRegistry(SoundEvent::class.java)
    sounds.values.forEach {
        it.registryName = it.soundName
        soundRegistry.register(it)
    }
}