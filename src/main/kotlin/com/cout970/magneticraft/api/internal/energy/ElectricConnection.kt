package com.cout970.magneticraft.api.internal.energy


import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.util.vector.length
import com.cout970.magneticraft.util.vector.minus

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
        if (firstNode.world.isClient) return

        //total resistance of the connection
        val R = (firstNode.resistance + secondNode.resistance) * separationDistance
        //capacity in the connection
        val C = 1.0 / (1.0 / firstNode.capacity + 1.0 / secondNode.capacity)
        //voltage difference
        val V = (firstNode.voltage * firstNode.capacity + secondNode.voltage * secondNode.capacity) / (firstNode.capacity + secondNode.capacity) - secondNode.voltage
        //intensity or amperage
        val I = ((1 - Math.exp(-1 / (R * C))) * V * secondNode.capacity / firstNode.capacity) * C * 2

        //the charge is moved
        firstNode.applyCurrent(-I)
        secondNode.applyCurrent(I)
    }

    override fun equals(other: Any?): Boolean {
        if (other is IElectricConnection) {
            return firstNode == other.firstNode && secondNode == other.secondNode
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return (super.hashCode() * 31 + firstNode.hashCode()) * 31 + secondNode.hashCode()
    }

    override fun toString(): String {
        return "ElectricConnection(firstNode=$firstNode, secondNode=$secondNode)"
    }
}