package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.GuiGuideBook
import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.LinkInfo
import com.cout970.magneticraft.guide.Page

class Link(val target: LinkInfo, val base: PageComponent) : PageComponent(base.position) {
    override val size = base.size

    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: Page.Gui) : PageComponent.Gui(parent) {
        val entryTarget = target.getEntryTarget()

        val base = this@Link.base.toGuiComponent(parent)

        override fun initGui() {
            super.initGui()
            base.initGui()
        }

        override fun draw(mouse: Coords, time: Double) {
            base.draw(mouse, time)
        }

        override fun postDraw(mouse: Coords, time: Double) {
            //TODO: Override with link target?
            base.postDraw(mouse, time)
        }

        override fun onLeftClick(mouse: Coords): Boolean {
            parent.gui.mc.displayGuiScreen(GuiGuideBook(entryTarget))

            return true
        }
    }
}