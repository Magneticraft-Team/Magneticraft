package com.cout970.magneticraft.api.internal.energy

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.util.hasIntersection
import com.cout970.magneticraft.util.vector.toVec3d
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/06/29.
 */
class WireConnectorWrapper(val node: IElectricNode,
                           val connectors: List<IVector3>) : IElectricNode by node, IWireConnector {

    override fun getConnectors(): ImmutableList<Vec3d> = ImmutableList.copyOf(connectors)

    override fun getConnectorsSize(): Int = connectors.size

    override fun getConnectorIndex(index: Int, connector: IWireConnector, connection: IElectricConnection): Int {

        val intersects = hasIntersection(
                aFirst = connectors.first().add(pos.toVec3d()),
                aSecond = connector.connectors.first().add(connector.pos.toVec3d()),
                bFirst = connectors.last().add(pos.toVec3d()),
                bSecond = connector.connectors.last().add(connector.pos.toVec3d())
        )
        return if (intersects) 2 - index else index
    }
}