package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.GuiShelvingUnit
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.gui.common.ContainerShelvingUnit
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import org.lwjgl.opengl.GL11


/**
 * Created by cout970 on 2017/07/29.
 */
class CompShelvingUnit(
        val container: ContainerShelvingUnit,
        val scrollBar: CompScrollBar
) : IComponent {

    override val box: Box = Vec2d.ZERO to Vec2d.ZERO
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val scroll = scrollBar.getScroll() / 19f
        if (container.scroll != scroll) {
            container.withScroll(scroll)
            container.sendUpdate(container.sendDataToServer()!!)
        }
        val column = Math.round(scroll * ((container.currentSlots.size / 9f) - 5))

        gui.bindTexture(GuiShelvingUnit.TEXTURE)

        GL11.glColor4f(1f, 1f, 1f, 1f)

        (0 until 5 * 9).forEach {
            val pos = it + column * 9
            if (pos >= container.currentSlots.size) {
                val x = it % 9 * 18 + 8
                val y = it / 9 * 18 + 21

                gui.drawScaledTexture(gui.box.pos + vec2Of(x, y), vec2Of(16, 16), vec2Of(240, 15), vec2Of(256, 256))
            }
        }
    }
}