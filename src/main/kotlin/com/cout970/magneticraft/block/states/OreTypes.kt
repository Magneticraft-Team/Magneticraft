package com.cout970.magneticraft.block.states

import net.minecraft.util.IStringSerializable

enum class OreTypes : IStringSerializable {
    COPPER,
    LEAD,
    COBALT,
    TUNGSTEN;

    override fun getName() = name.toLowerCase()
}