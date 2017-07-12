package com.cout970.magneticraft.registry

import com.cout970.magneticraft.util.resource
import net.minecraft.util.SoundEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries

//MAp with all the sounds in the mod
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
fun registerSounds() {
    sounds.values.forEach {
        it.registryName = it.soundName
        ForgeRegistries.SOUND_EVENTS.register(it)
    }
}