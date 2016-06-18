package com.cout970.magneticraft.api.energy

/**
 * Created by cout970 on 11/06/2016.
 */
interface IElectricNode : INode {

    fun getVoltage():Double

    fun getAmperage():Double

    fun getResistance():Double

    fun applyCurrent(current: Double)

    fun applyPower(power :Double)
}