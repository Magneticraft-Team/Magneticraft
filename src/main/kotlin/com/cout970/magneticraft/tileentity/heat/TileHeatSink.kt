package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister

/**
 * Created by cout970 on 04/07/2016.
 */
@TileRegister("heat_sink")
class TileHeatSink : TileBase() {

    val heat = HeatContainer(dissipation = 0.05,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT,
            conductivity = DEFAULT_CONDUCTIVITY,
            worldGetter = { this.world },
            posGetter = { this.getPos() })

    val traitHeat: TraitHeat = TraitHeat(this, listOf(heat))

    override val traits: List<ITileTrait> = listOf(traitHeat)
}