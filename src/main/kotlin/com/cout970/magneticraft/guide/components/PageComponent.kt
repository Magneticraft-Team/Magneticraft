package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.BookPage
import com.cout970.magneticraft.util.vector.Vec2d

abstract class PageComponent(val position: Vec2d) {

    //name of the component
    abstract val id: String
    //size of the component
    abstract val size: Vec2d

    abstract fun toGuiComponent(parent: BookPage.Gui): GuiPageComponent

    override fun toString(): String{
        return "PageComponent(id='$id', position=$position, size=$size)"
    }

    protected abstract inner class Gui(val parent: BookPage.Gui) : GuiPageComponent {

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