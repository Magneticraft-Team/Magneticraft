package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.vector.length
import com.cout970.magneticraft.misc.vector.minus

/**
 * Created by cout970 on 11/06/2016.
 */
open class HeatConnection(
    private val firstNode: IHeatNode,
    private val secondNode: IHeatNode
) : IHeatConnection {

    override fun getFirstNode() = firstNode
    override fun getSecondNode() = secondNode

    override fun getSeparationDistance() = (firstNode.pos - secondNode.pos).length

    override fun iterate() {

        val conductivity = 1 / (1 / firstNode.conductivity + 1 / secondNode.conductivity)
        val tempDiff = firstNode.temperature - secondNode.temperature
        val heatTransfer = -conductivity * tempDiff / separationDistance

        secondNode.applyHeat(-heatTransfer)
        firstNode.applyHeat(heatTransfer)
    }

    override fun equals(other: Any?): Boolean {
        if (other is IHeatConnection) {
            return firstNode == other.firstNode && secondNode == other.secondNode
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return (super.hashCode() * 31 + firstNode.hashCode()) * 31 + secondNode.hashCode()
    }

    override fun toString(): String {
        return "HeatConnection(firstNode=$firstNode, secondNode=$secondNode)"
    }
}