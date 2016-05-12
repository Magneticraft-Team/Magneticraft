package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page
import net.minecraft.item.ItemStack

class Icon(
    position: Coords,
    val stack: ItemStack
) : PageComponent(position) {
    override val size = Coords(16, 16)

    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: Page.Gui) : PageComponent.Gui(parent) {
        override fun draw(mouse: Coords, time: Double) {
            parent.gui.drawStack(stack, drawPos)
        }

        override fun postDraw(mouse: Coords, time: Double) {
            if (mouse.inside(drawPos, drawPos + size)) {
                parent.gui.renderToolTip(stack, mouse)
            }
        }
    }
}