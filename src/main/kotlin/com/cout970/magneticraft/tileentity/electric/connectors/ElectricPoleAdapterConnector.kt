package com.cout970.magneticraft.tileentity.electric.connectors

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.BlockElectricPoleAdapter
import com.cout970.magneticraft.block.ELECTRIC_POLE_PLACE
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.util.vector.times
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 06/07/2016.
 */
class ElectricPoleAdapterConnector(val node: ElectricNode) : IElectricNode by node, IWireConnector {

    override fun getConnectors(): ImmutableList<Vec3d> {
        val state = world.getBlockState(pos)
        if (state.block != BlockElectricPoleAdapter)
            return ImmutableList.of()
        val offset = state[ELECTRIC_POLE_PLACE].offset.rotateYaw(Math.toRadians(-90.0).toFloat())
        val vec = Vec3d(0.5, 1.0 - 0.0625 * 6.5, 0.5).add(offset * 0.5)
        return ImmutableList.of(vec)
    }

    override fun getConnectorsSize(): Int = 1

    override fun equals(other: Any?): Boolean {
        return super.equals(other) || if (other is ElectricNode) node.equals(other) else false
    }

    override fun hashCode(): Int {
        return node.hashCode()
    }

    override fun toString() = node.toString()
}