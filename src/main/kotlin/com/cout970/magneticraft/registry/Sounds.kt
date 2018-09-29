package com.cout970.magneticraft.registry

import com.cout970.magneticraft.misc.resource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.registries.IForgeRegistry

enum class Sounds(
    val resourceLocation: ResourceLocation,
    val soundEvent: SoundEvent = SoundEvent(resourceLocation)
) {
    CRUSHING_HIT(resource("crushing_hit")),
    CRUSHING_FINAL(resource("crushing_final")),
    WATER_FLOW(resource("water_flow")),
    WATER_FLOW_END(resource("water_flow_end"))
}

/**
 * Called by ClientProxy to register all the sounds
 */
fun registerSounds(registry: IForgeRegistry<SoundEvent>) {
    Sounds.values().forEach {
        it.soundEvent.registryName = it.soundEvent.soundName
        registry.register(it.soundEvent)
    }
}