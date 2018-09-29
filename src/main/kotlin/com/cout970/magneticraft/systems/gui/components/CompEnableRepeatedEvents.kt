package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import org.lwjgl.input.Keyboard

class CompEnableRepeatedEvents : IComponent {

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d.ZERO
    override lateinit var gui: IGui

    init {
        Keyboard.enableRepeatEvents(true)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) = Unit

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }
}