package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.util.clamp
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 09/07/2016.
 */
class CompElectricBar(val node: IElectricNode, pos: Vec2d) : CompVerticalBar(ElectricBarProvider(node), 0, pos, tooltip = {listOf(String.format("%.2fV", node.voltage))})

class ElectricBarProvider(val node: IElectricNode) : IBarProvider {
    override fun getLevel(): Float = clamp(node.voltage / 120.0, 1.0, 0.0).toFloat()
}
