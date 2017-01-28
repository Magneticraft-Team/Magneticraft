package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.InfiniteHeatContainer
import com.cout970.magneticraft.util.toKelvinFromCelsius

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileInfiniteHeat(temperature: Double, emit: Boolean = false) : TileHeatBase() {

    val heat = InfiniteHeatContainer(
            temperature = temperature,
            tile = this,
            emit = emit)

    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)
}

//Minecraft needs an empty constructor to be able to load TileEntities from save files
class TileInfiniteHeatCold : TileInfiniteHeat(temperature = (-50).toKelvinFromCelsius(), emit = false)
class TileInfiniteHeatHot : TileInfiniteHeat(1800.toKelvinFromCelsius())