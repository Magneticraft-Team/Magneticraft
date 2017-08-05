package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.debug
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/03.
 */
class CompTextLine(
        override val pos: IVector2,
        val text: String,
        val color: Int = 0xFFFFFF
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 get() = vec2Of(gui.fontHelper.getStringWidth(text), gui.fontHelper.FONT_HEIGHT)

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        debug(gui.pos, pos, gui.pos + pos)
        gui.drawString(text, gui.pos + pos, color)
    }
}