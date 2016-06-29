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
    private val separationDistance = (firstNode.pos - secondNode.pos).length

    override fun getFirstNode() = firstNode
    override fun getSecondNode() = secondNode
    override fun getSeparationDistance() = (firstNode.pos - secondNode.pos).length

    override fun iterate() {
        //total resistance of the connection
        val R = (firstNode.resistance + secondNode.resistance) * separationDistance
        //number of iterations needed to avoid instabilities with voltages
        val times = (2 / R).toInt()
        for (i in 0..times - 1) {
            //voltage difference between the conductors
            val V = firstNode.voltage - secondNode.voltage
            //Ohm's law with 'times' to divide the amperage in the different iterations of the for loop
            val I = (V / R) / times
            //moves the charge between the conductors
            //this follows the Kirchhoff's first law
            firstNode.applyCurrent(-I)
            secondNode.applyCurrent(I)
        }
    }
}