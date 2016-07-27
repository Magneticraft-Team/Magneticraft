package com.cout970.magneticraft.integration.tesla

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.util.TIER_1_MAX_VOLTAGE
import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer

/**
 * Created by cout970 on 27/07/2016.
 */
class TeslaNodeWrapper(
        val node: IElectricNode
) : ITeslaHolder, ITeslaProducer, ITeslaConsumer {

    override fun getCapacity(): Long = (node.capacity * TIER_1_MAX_VOLTAGE * TIER_1_MAX_VOLTAGE / 2).toLong()

    override fun getStoredPower(): Long = (node.capacity * node.voltage * node.voltage / 2).toLong()

    override fun takePower(power: Long, simulated: Boolean): Long {
        return Math.ceil(node.applyPower(-power.toDouble(), simulated)).toLong()
    }

    override fun givePower(power: Long, simulated: Boolean): Long {
        var give = power
        if (give + storedPower >= capacity) {
            give = capacity - storedPower
        }
        return Math.ceil(node.applyPower(give.toDouble(), simulated)).toLong()
    }
}