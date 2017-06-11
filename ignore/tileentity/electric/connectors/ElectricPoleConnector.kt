package com.cout970.magneticraft.tileentity.electric.connectors

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.ELECTRIC_POLE_PLACE
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.util.hasIntersection
import com.cout970.magneticraft.util.vector.toVec3d
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 06/07/2016.
 */

class ElectricPoleConnector(val node: ElectricNode) : IElectricNode by node, IWireConnector {

    override fun getConnectors(): ImmutableList<Vec3d> {
        val state = world.getBlockState(pos)
        if(!ELECTRIC_POLE_PLACE.isIn(state))
            return ImmutableList.of()
        val offset = state[ELECTRIC_POLE_PLACE].offset
        val first = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).add(offset)
        val center = Vec3d(0.5, 1.0, 0.5)
        val last = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).subtract(offset)
        return ImmutableList.of(first, center, last)
    }

    override fun getConnectorsSize(): Int = 3

    override fun getConnectorIndex(index: Int, connector: IWireConnector, connection: IElectricConnection): Int {

        if (hasIntersection(connectors.first().add(pos.toVec3d()), connector.connectors.first().add(connector.pos.toVec3d()),
                connectors.last().add(pos.toVec3d()), connector.connectors.last().add(connector.pos.toVec3d()))) {
            return 2 - index
        }
        return index
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) || if (other is ElectricNode) node.equals(other) else false
    }

    override fun hashCode(): Int {
        return node.hashCode()
    }

    override fun toString() = node.toString()
}