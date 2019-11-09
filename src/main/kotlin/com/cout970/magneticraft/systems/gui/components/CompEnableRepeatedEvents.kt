package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import com.cout970.magneticraft.systems.gui.render.keyboardEnableRepeatedEvents

class CompEnableRepeatedEvents : IComponent {

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d.ZERO
    override lateinit var gui: IGui

    init {
        keyboardEnableRepeatedEvents(true)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) = Unit

    override fun onGuiClosed() {
        keyboardEnableRepeatedEvents(false)
    }
}