package com.cout970.magneticraft.misc.energy

class RfNodeWrapper(val storage: RfStorage) : IMachineEnergyInterface {

    override fun getSpeed(): Double = storage.energyStored / storage.maxEnergyStored.toDouble()

    override fun hasEnergy(amount: Double): Boolean {
        return storage.energyStored > amount.toInt()
    }

    override fun useEnergy(amount: Double) {
        storage.extractEnergy(amount.toInt(), false)
    }
}