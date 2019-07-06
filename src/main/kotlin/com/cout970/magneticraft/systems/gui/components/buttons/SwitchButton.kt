package com.cout970.magneticraft.systems.gui.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui

class SwitchButton(
    override val pos: IVector2,
    val enableIcon: IVector2,
    val disableIcon: IVector2,
    val tooltipEnable: String? = null,
    val tooltipDisable: String? = null,
    val id: String = "no-id",
    var onClick: () -> Unit = {},
    var isEnable: () -> Boolean = { false }
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 = vec2Of(18, 18)
    val backgroundEnable = vec2Of(36, 100)
    val backgroundDisable = vec2Of(55, 100)
    val hoverUV = vec2Of(183, 10)

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val enable = isEnable()
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(
            gui.pos + pos,
            size,
            if (enable) backgroundEnable else backgroundDisable
        )

        gui.drawTexture(
            gui.pos + pos + vec2Of(1, 1),
            vec2Of(16, 16),
            if (enable) enableIcon else disableIcon
        )
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            gui.bindTexture(guiTexture("misc"))
            gui.drawTexture(
                pos,
                size,
                hoverUV
            )

            val txt = if (isEnable()) tooltipEnable else tooltipDisable
            if (txt != null) {
                gui.drawHoveringText(listOf(txt), mouse)
            }
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        val inBounds = mouse in (gui.pos + pos to size)

        if (inBounds) onClick()
        return inBounds
    }
}