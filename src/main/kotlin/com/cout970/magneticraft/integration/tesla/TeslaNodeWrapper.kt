package com.cout970.magneticraft.integration.tesla

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_MAX_VOLTAGE
import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer

/**
 * Created by cout970 on 27/07/2016.
 */
class TeslaNodeWrapper(
        val node: IElectricNode
) : ITeslaHolder, ITeslaProducer, ITeslaConsumer {

    override fun getCapacity(): Long {
        return (Config.wattsToTesla * node.capacity * TIER_1_MAX_VOLTAGE * TIER_1_MAX_VOLTAGE / 2).toLong()
    }

    override fun getStoredPower(): Long = (Config.wattsToTesla * node.capacity * node.voltage * node.voltage / 2).toLong()

    override fun takePower(power: Long, simulated: Boolean): Long {
        return Math.ceil(node.applyPower(-power.toDouble() / Config.wattsToTesla, simulated)).toLong()
    }

    override fun givePower(power: Long, simulated: Boolean): Long {
        var give = power / Config.wattsToTesla
        if (give + storedPower >= capacity) {
            give = capacity - storedPower.toDouble()
        }
        return Math.ceil(node.applyPower(give, simulated)).toLong()
    }
}