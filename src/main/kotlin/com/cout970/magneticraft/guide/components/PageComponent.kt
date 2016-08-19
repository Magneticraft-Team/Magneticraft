package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page
import com.cout970.magneticraft.util.vector.Vec2d

abstract class PageComponent(val position: Vec2d) {

    abstract val size: Vec2d

    abstract fun toGuiComponent(parent: Page.Gui): GuiPageComponent

    protected abstract inner class Gui(val parent: Page.Gui) : GuiPageComponent {

        override val size = this@PageComponent.size
        override val position = this@PageComponent.position

        lateinit var drawPos: Vec2d
            private set

        override fun initGui() {
            drawPos = parent.start + position
        }

        override fun isMouseInside(mouse: Vec2d) = mouse in drawPos to (drawPos + size)

        override fun onLeftClick(mouse: Vec2d) = false

        override fun postDraw(mouse: Vec2d, time: Double) = Unit
    }
}