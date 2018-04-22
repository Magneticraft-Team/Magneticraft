package com.cout970.magneticraft.api.internal.registries.tool.hammer

import com.cout970.magneticraft.api.registries.tool.hammer.IHammer

data class Hammer(val level: Int, val speed: Int, val cost: Int) : IHammer {

    override fun getMiningLevel(): Int = level

    override fun getBreakingSpeed(): Int = speed

    override fun getDurabilityCost(): Int = cost
}