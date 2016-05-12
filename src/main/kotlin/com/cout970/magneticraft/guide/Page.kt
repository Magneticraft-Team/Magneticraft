package com.cout970.magneticraft.guide

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.GuiGuideBook
import com.cout970.magneticraft.gui.client.guide.PAGE_SIZE
import com.cout970.magneticraft.guide.components.PageComponent

class Page(private val components: List<PageComponent>) {
    fun toGuiComponent(parent: GuiGuideBook, page: GuiGuideBook.Page) = Gui(parent, page)

    inner class Gui(val gui: GuiGuideBook, val page: GuiGuideBook.Page) {
        lateinit var start: Coords
        val size = PAGE_SIZE
        val components = this@Page.components.map { it.toGuiComponent(this) }

        fun initGui() {
            start = page.start + gui.start
            components.forEach { it.initGui() }
        }

        fun draw(mouse: Coords, time: Double) {
            components.forEach { it.draw(mouse, time) }
        }

        fun postDraw(mouse: Coords, time: Double) {
            components.forEach { it.postDraw(mouse, time) }
        }

        fun onLeftClick(mouse: Coords) =
            components.filter { it.isMouseInside(mouse) }.any { it.onLeftClick(mouse) }
    }
}