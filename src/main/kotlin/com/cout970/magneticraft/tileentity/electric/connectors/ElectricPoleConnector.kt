package com.cout970.magneticraft.tileentity.electric.connectors

import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.ELECTRIC_POLE_PLACE
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.hasIntersection
import com.cout970.magneticraft.util.isIn
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 06/07/2016.
 */

class ElectricPoleConnector(node: ElectricNode) : IElectricNode by node, IWireConnector {

    override fun getConnectors(): ImmutableList<Vec3d> {
        val state = world.getBlockState(pos)
        if(!ELECTRIC_POLE_PLACE.isIn(state))
            return ImmutableList.of()
        val offset = ELECTRIC_POLE_PLACE[state].offset
        val first = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).add(offset)
        val center = Vec3d(0.5, 1.0, 0.5)
        val last = Vec3d(0.5, 1.0 - 0.0625 * 4, 0.5).subtract(offset)
        return ImmutableList.of(first, center, last)
    }

    override fun getConnectorsSize(): Int = 3

    override fun getConnectorIndex(index: Int, connector: IWireConnector, connection: IElectricConnection): Int {

        if (hasIntersection(connectors.first().add(pos.toDoubleVec()), connector.connectors.first().add(connector.pos.toDoubleVec()),
                connectors.last().add(pos.toDoubleVec()), connector.connectors.last().add(connector.pos.toDoubleVec()))) {
            return 2 - index
        }
        return index
    }
}