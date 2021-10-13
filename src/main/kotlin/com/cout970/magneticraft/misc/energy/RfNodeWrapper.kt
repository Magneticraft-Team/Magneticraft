package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.systems.config.Config

class RfNodeWrapper(val storage: RfStorage) : IMachineEnergyInterface {

    override fun getSpeed(): Double = if(Config.machineConstantSpeed) storage.energyStored / storage.maxEnergyStored.toDouble() else 1.0

    override fun hasEnergy(amount: Double): Boolean {
        return storage.energyStored > amount.toInt()
    }

    override fun useEnergy(amount: Double) {
        storage.extractEnergy(amount.toInt(), false)
    }
}