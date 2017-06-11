package com.cout970.magneticraft.registry

import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry


val fuels = mapOf<Item, Int>()

fun registerFuelHandler() {
    GameRegistry.registerFuelHandler { fuel ->
        fuels.getOrDefault(fuel.item, 0)
    }
}