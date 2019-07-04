package com.cout970.magneticraft.systems.gui.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import com.cout970.magneticraft.systems.gui.render.isMouseButtonDown

class ClickButton(
    override val pos: IVector2,
    val id: String = "no-id",
    var onClick: () -> Unit = {}
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 = vec2Of(18, 18)
    val backgroundUV = vec2Of(55, 100)
    val hoverUV = vec2Of(183, 10)
    val pressUV = vec2Of(164, 10)
    var state = States.NORMAL

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(
            gui.pos + pos,
            size,
            backgroundUV
        )

        state = if (mouse in (gui.pos + pos to size)) {
            if (isMouseButtonDown(0)) States.PRESS else States.HOVER
        } else {
            States.NORMAL
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (state == States.HOVER) {
            gui.bindTexture(guiTexture("misc"))
            gui.drawTexture(
                pos,
                size,
                hoverUV
            )

        } else if (state == States.PRESS) {
            gui.bindTexture(guiTexture("misc"))
            gui.drawTexture(
                pos,
                size,
                pressUV
            )
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        val inBounds = mouse in (gui.pos + pos to size)

        if (inBounds) onClick()
        return inBounds
    }

    enum class States {
        NORMAL, HOVER, PRESS
    }
}