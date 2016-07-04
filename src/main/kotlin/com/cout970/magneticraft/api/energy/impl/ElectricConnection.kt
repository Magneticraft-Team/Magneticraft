package com.cout970.magneticraft.api.energy.impl

import coffee.cypher.mcextlib.extensions.vectors.length
import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode

/**
 * Created by cout970 on 11/06/2016.
 */
open class ElectricConnection(
        private val firstNode: IElectricNode,
        private val secondNode: IElectricNode
) : IElectricConnection {

    override fun getFirstNode() = firstNode
    override fun getSecondNode() = secondNode
    override fun getSeparationDistance() = (firstNode.pos - secondNode.pos).length

    override fun iterate() {
        //total resistance of the connection
        val R = (firstNode.resistance + secondNode.resistance) * separationDistance
        //capacity of the connection
        val C = 0.5
        //voltage difference
        val V = firstNode.voltage - secondNode.voltage
        //final voltage
        val Vf = V / 2 * Math.exp(-1 / (R * C)) + V / 2 + secondNode.voltage
        //intensity or amperage
        val I = firstNode.voltage - Vf
        //the charge is moved, using the kirchhoff law
        firstNode.applyCurrent(-I)
        secondNode.applyCurrent(I)
    }
}