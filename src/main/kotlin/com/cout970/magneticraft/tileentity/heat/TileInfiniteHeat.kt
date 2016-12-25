package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.InfiniteHeatContainer

/**
 * Created by cout970 on 04/07/2016.
 */
class TileInfiniteHeat(temperature: Double, emit: Boolean = false) : TileHeatBase() {

    val heat = InfiniteHeatContainer(
            temperature = temperature,
            tile = this,
            emit = emit)

    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)
}