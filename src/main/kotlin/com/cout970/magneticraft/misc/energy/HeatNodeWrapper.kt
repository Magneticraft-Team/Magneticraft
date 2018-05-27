package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.tempToEnergy
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.WATER_BOILING_POINT

class HeatNodeWrapper(val node: IHeatNode) : IMachineEnergyInterface {

    override fun getSpeed(): Double {
        return if (node.temperature > WATER_BOILING_POINT) 1.0 else 0.0
    }

    override fun hasEnergy(amount: Double): Boolean {
        return tempToEnergy(node, node.temperature - STANDARD_AMBIENT_TEMPERATURE) > amount
    }

    override fun useEnergy(amount: Double) {
        node.applyHeat(-amount)
    }
}