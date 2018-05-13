package com.cout970.magneticraft.misc.energy

interface IMachineEnergyInterface {

    fun getSpeed(): Double

    fun hasEnergy(amount: Double): Boolean

    fun useEnergy(amount: Double)
}