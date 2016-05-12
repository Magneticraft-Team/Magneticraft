package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page

abstract class PageComponent(val position: Coords) {
    abstract val size: Coords

    abstract fun toGuiComponent(parent: Page.Gui): GuiPageComponent

    protected abstract inner class Gui(val parent: Page.Gui) : GuiPageComponent {
        override val size = this@PageComponent.size
        override val position = this@PageComponent.position

        lateinit var drawPos: Coords
            private set

        override fun initGui() {
            drawPos = parent.start + position
        }

        override fun isMouseInside(mouse: Coords) = mouse.inside(drawPos, drawPos + size)

        override fun onLeftClick(mouse: Coords) = false

        override fun postDraw(mouse: Coords, time: Double) {
        }
    }
}