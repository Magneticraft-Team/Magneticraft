package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_BATTERY_CHARGE_VOLTAGE
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE

class ElectricNodeWrapper(val node: IElectricNode) : IMachineEnergyInterface {

    override fun getSpeed(): Double {
        val s = (node.voltage - TIER_1_MACHINES_MIN_VOLTAGE) / (TIER_1_BATTERY_CHARGE_VOLTAGE - TIER_1_MACHINES_MIN_VOLTAGE)
        return s.coerceIn(0.0, 1.0)
    }

    override fun hasEnergy(amount: Double): Boolean {
        val newVoltage = Math.sqrt(node.voltage * node.voltage - Math.abs(amount) * 2)
        return newVoltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
    }

    override fun useEnergy(amount: Double) {
        node.applyPower(-amount, false)
    }
}