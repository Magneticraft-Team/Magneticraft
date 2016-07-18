package com.cout970.magneticraft.tileentity.electric.connectors

import coffee.cypher.mcextlib.extensions.vectors.plus
import coffee.cypher.mcextlib.extensions.vectors.times
import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import com.cout970.loader.impl.util.PIXEL
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.google.common.collect.ImmutableList
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 06/07/2016.
 */
class ElectricConnector(node: ElectricNode, val tile: TileElectricConnector) : IElectricNode by node, IWireConnector {

    override fun getConnectors(): ImmutableList<Vec3d> {
        val vec = Vec3d(0.5, 0.5, 0.5) + (tile.getFacing().directionVec.toDoubleVec() * PIXEL * 3.0)
        return ImmutableList.of(vec)
    }

    override fun getConnectorsSize(): Int = 1
}