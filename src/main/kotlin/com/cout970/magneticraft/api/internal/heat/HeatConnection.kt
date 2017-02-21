package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatNode

/**
 * Created by cout970 on 11/06/2016.
 */
open class HeatConnection(
        private val firstNode: IHeatNode,
        private val secondNode: IHeatNode
) : IHeatConnection {

    override fun getFirstNode() = firstNode
    override fun getSecondNode() = secondNode

    override fun iterate() {
        //Use the lowest conductivity.
        val minConductivity = Math.min(firstNode.conductivity, secondNode.conductivity)
        if (minConductivity <= 0) return
        //Heat moving in the other direction is handled in the other entity
        if (firstNode.temperature > secondNode.temperature) {
            val tempDiff = Math.abs(firstNode.temperature - secondNode.temperature)
            var heatTransfer = Math.floor(tempDiff * minConductivity)
            //simulated, this is used to get the min between
            //the amount that 'second' can accept and the amount that 'first' can lose
            heatTransfer = secondNode.applyHeat(heatTransfer, true)
            heatTransfer = firstNode.applyHeat(-heatTransfer, true)
            //no simulated
            secondNode.applyHeat(heatTransfer, false)
            firstNode.applyHeat(-heatTransfer, false)
        }
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