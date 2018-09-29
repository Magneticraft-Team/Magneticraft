package com.cout970.magneticraft.systems.config

/**
 * Created by cout970 on 11/06/2016.
 */
class GaussOreConfig(
    val minAmountPerChunk: Int,
    val maxAmountPerChunk: Int,
    val deviation: Float,
    chunkAmount: Int,
    veinAmount: Int,
    maxLevel: Int,
    minLevel: Int,
    active: Boolean = true
) : OreConfig(chunkAmount, veinAmount, maxLevel, minLevel, active)