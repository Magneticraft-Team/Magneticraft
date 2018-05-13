package com.cout970.magneticraft.misc.energy

import net.minecraftforge.energy.EnergyStorage

class RfStorage(cap: Int) : EnergyStorage(cap) {

    fun setEnergyStored(a: Int) {
        energy = a
    }
}