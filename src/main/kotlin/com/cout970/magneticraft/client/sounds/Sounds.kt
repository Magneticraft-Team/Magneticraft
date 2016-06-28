package com.cout970.magneticraft.client.sounds

import com.cout970.magneticraft.util.resource
import net.minecraft.util.SoundEvent
import net.minecraftforge.fml.common.registry.GameRegistry

val sounds = listOf(
    "crushing_hit",
    "crushing_final"
).associate {
    it to SoundEvent(resource(it))
}

fun registerSounds() {
    sounds.values.forEach {
        GameRegistry.register(it, it.soundName)
    }
}