package com.cout970.magneticraft.api.energy

/**
 * Created by cout970 on 11/06/2016.
 */
interface IElectricConection {

    fun getFirstNode(): IElectricNode

    fun getSecondNode(): IElectricNode

    fun getSeparationDistance(): Double

    fun iterate()
}