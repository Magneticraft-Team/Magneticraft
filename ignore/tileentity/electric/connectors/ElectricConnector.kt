package com.cout970.magneticraft.tileentity.electric.connectors

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.tilerenderer.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.times
import com.cout970.magneticraft.util.vector.toVec3d
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 06/07/2016.
 */
class ElectricConnector(val node: ElectricNode, val tile: TileElectricConnector) : IElectricNode by node, IWireConnector {

    override fun getConnectors(): ImmutableList<Vec3d> {
        val offset = if (!tile.isInvalid) (tile.getFacing().directionVec.toVec3d() * PIXEL * 3.0) else Vec3d.ZERO
        val vec = Vec3d(0.5, 0.5, 0.5) + offset
        return ImmutableList.of(vec)
    }

    override fun getConnectorsSize(): Int = 1

    override fun toString() = node.toString()
}