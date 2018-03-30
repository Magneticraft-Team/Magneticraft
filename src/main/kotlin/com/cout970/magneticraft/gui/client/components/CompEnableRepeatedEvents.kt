package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.vector.Vec2d
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