package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.gui.common.ContainerShelvingUnit
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_SCROLL
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import org.lwjgl.opengl.GL11


/**
 * Created by cout970 on 2017/07/29.
 */
class CompShelvingUnit(
        val container: ContainerShelvingUnit,
        val scrollBar: CompScrollBar,
        val textInput: CompTextInput
) : IComponent {

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d.ZERO
    override lateinit var gui: IGui

    init {
        textInput.updateFunc = { container.setFilter(it.text )}
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val scroll = scrollBar.getScroll() / 19f
        if (container.scroll != scroll) {
            container.withScroll(scroll)
            container.sendUpdate(IBD().apply { setFloat(DATA_ID_SHELVING_UNIT_SCROLL, scroll) })
        }
        val column = Math.max(0, Math.round(scroll * ((container.currentSlots.size / 9f) - 5)))
        gui.bindTexture(guiTexture("shelving_unit"))

        GL11.glColor4f(1f, 1f, 1f, 1f)

        (0 until 5 * 9).forEach {
            val pos = it + column * 9
            if (pos >= container.currentSlots.size) {
                val x = it % 9 * 18 + 8
                val y = it / 9 * 18 + 21

                gui.drawTexture(DrawableBox(
                        screen = Pair(gui.pos + vec2Of(x, y), vec2Of(16, 16)),
                        texture = Pair(vec2Of(240, 15), vec2Of(16, 16)),
                        textureSize = vec2Of(256, 256)
                ))
            }
        }
    }
}