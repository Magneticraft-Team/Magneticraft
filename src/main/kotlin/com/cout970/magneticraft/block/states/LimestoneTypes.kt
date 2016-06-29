package com.cout970.magneticraft.block.states

import net.minecraft.util.IStringSerializable

enum class LimestoneTypes : IStringSerializable {
    NORMAL,
    BRICK,
    COBBLE;

    override fun getName() = name.toLowerCase()
}