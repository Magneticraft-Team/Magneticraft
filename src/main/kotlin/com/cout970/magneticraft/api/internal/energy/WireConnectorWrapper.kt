package com.cout970.magneticraft.api.internal.energy

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.misc.hasIntersection
import com.cout970.magneticraft.misc.vector.toVec3d
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/06/29.
 */
class WireConnectorWrapper(
    val node: IElectricNode,
    val connectors: () -> List<IVector3>,
    val name: String
) : IElectricNode by node, IWireConnector {

    override fun getId(): NodeID = NodeID(name, pos, world)

    override fun getConnectors(): ImmutableList<Vec3d> = ImmutableList.copyOf(connectors())

    override fun getConnectorsSize(): Int = connectors().size

    override fun getConnectorIndex(index: Int, connector: IWireConnector, connection: IElectricConnection): Int {

        val points = connectors()
        if (points.size == 3) {
            val intersects = hasIntersection(
                aFirst = points.first().add(pos.toVec3d()),
                aSecond = connector.connectors.first().add(connector.pos.toVec3d()),
                bFirst = points.last().add(pos.toVec3d()),
                bSecond = connector.connectors.last().add(connector.pos.toVec3d())
            )
            return if (intersects) 2 - index else index
        }
        return index
    }
}