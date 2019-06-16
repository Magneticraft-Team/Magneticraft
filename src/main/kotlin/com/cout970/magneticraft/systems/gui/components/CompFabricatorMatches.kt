package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.features.manual_machines.TileFabricator
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.iterateArea
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui

class CompFabricatorMatches(val tile: TileFabricator) : IComponent {
    override val pos: IVector2 = vec2Of(17, 14)
    override val size: IVector2 = vec2Of(18 * 3)
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val matches = tile.fabricatorModule.itemMatches ?: return
        if (tile.fabricatorModule.craftingResult[0].isEmpty) return

        val red = 0x7FFF0000
        var i = 0

        iterateArea(0..2, 0..2) { x, y ->
            if (!matches[i++]) {
                val start = gui.pos + vec2Of(x * 18 + pos.x, y * 18 + pos.y)
                val end = start + vec2Of(16)
                gui.drawColor(start, end, red)
            }
        }
    }
}