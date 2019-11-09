package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_BATTERY_CHARGE_VOLTAGE
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
import kotlin.math.abs
import kotlin.math.sqrt

class ElectricNodeWrapper(val node: IElectricNode) : IMachineEnergyInterface {

    override fun getSpeed(): Double {
        val s = (node.voltage - TIER_1_MACHINES_MIN_VOLTAGE) / (TIER_1_BATTERY_CHARGE_VOLTAGE - TIER_1_MACHINES_MIN_VOLTAGE)
        return s.coerceIn(0.0, 1.0)
    }

    override fun hasEnergy(amount: Double): Boolean {
        val newVoltage = sqrt(node.voltage * node.voltage - abs(amount) * 2)
        return newVoltage >= TIER_1_MACHINES_MIN_VOLTAGE
    }

    override fun useEnergy(amount: Double) {
        node.applyPower(-amount, false)
    }
}