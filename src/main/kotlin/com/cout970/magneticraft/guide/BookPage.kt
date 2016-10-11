package com.cout970.magneticraft.guide

import com.cout970.magneticraft.gui.client.guide.GuiGuideBook
import com.cout970.magneticraft.guide.components.PageComponent
import com.cout970.magneticraft.util.vector.Vec2d

data class BookPage(private val components: List<PageComponent>) {

    fun toGuiComponent(parent: GuiGuideBook, page: GuiGuideBook.Page) = Gui(parent, page)

    inner class Gui(val gui: GuiGuideBook, val page: GuiGuideBook.Page) {
        lateinit var start: Vec2d
        val size = PAGE_SIZE
        val components = this@BookPage.components.map { it.toGuiComponent(this) }

        fun initGui() {
            start = page.start + gui.start
            components.forEach { it.initGui() }
        }

        fun draw(mouse: Vec2d, time: Double) {
            components.forEach { it.draw(mouse, time) }
        }

        fun postDraw(mouse: Vec2d, time: Double) {
            components.forEach { it.postDraw(mouse, time) }
        }

        fun onLeftClick(mouse: Vec2d) =
            components.filter { it.isMouseInside(mouse) }.any { it.onLeftClick(mouse) }
    }
}