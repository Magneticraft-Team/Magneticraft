package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.tileentity.TileBase
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister

/**
 * Created by cout970 on 27/07/2016.
 */
@TileRegister("infinite_energy")
class TileInfiniteEnergy : TileBase() {

    var mainNode = object : ElectricNode({ world }, { pos }, capacity = 1.25) {
        override fun getVoltage(): Double {
            return ElectricConstants.TIER_1_MAX_VOLTAGE
        }

        override fun applyCurrent(current: Double) {
            amperageCount += Math.abs(current) * 2
        }

        override fun applyPower(power: Double, simulated: Boolean): Double = power
    }

    val traitElectricity = TraitElectricity(this, listOf(mainNode))

    override val traits: List<ITileTrait> = listOf(traitElectricity)
}