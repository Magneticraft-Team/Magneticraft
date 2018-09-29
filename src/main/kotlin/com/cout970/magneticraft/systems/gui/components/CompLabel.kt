package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui

class CompLabel(
    override val pos: IVector2,
    val color: Int = 0xFFFFFF,
    val text: () -> String
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 get() = vec2Of(gui.fontHelper.getStringWidth(text()), gui.fontHelper.FONT_HEIGHT)

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.drawString(text(), gui.pos + pos, color)
    }
}