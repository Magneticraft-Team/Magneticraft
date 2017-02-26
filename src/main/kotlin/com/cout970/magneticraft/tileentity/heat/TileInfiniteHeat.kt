package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.internal.heat.InfiniteHeatContainer
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.toKelvinFromCelsius

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileInfiniteHeat(temperature: Double) : TileBase() {

    val heat = InfiniteHeatContainer(this, temperature = temperature)

    val traitHeat: TraitHeat = TraitHeat(this, listOf(heat))

    override val traits: List<ITileTrait> = listOf(traitHeat)
}

//Minecraft needs an empty constructor to be able to load TileEntities from save files
class TileInfiniteHeatCold : TileInfiniteHeat((-50).toKelvinFromCelsius())

class TileInfiniteHeatHot : TileInfiniteHeat(1800.toKelvinFromCelsius())