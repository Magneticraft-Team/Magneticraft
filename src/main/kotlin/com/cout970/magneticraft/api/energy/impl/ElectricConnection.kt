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
        if (firstNode.world.isRemote) return
        //total resistance of the connection
        val R = (firstNode.resistance + secondNode.resistance) * separationDistance
        //capacity in the connection
        val C = 1.0 / (1.0 / firstNode.capacity + 1.0 / secondNode.capacity)
        //voltage difference
        val V = (firstNode.voltage * firstNode.capacity + secondNode.voltage * secondNode.capacity) / (firstNode.capacity + secondNode.capacity) - secondNode.voltage
        //intensity or amperage
        var I = (1 - Math.exp(-1 / (R * C))) * V * secondNode.capacity / firstNode.capacity
        I *= C * 2
        //the charge is moved
        firstNode.applyCurrent(-I)
        secondNode.applyCurrent(I)
    }
}