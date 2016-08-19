package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.item.ItemStack

class StackIcon(
        position: Vec2d,
        val stack: ItemStack
) : PageComponent(position) {

    override val size = Vec2d(16, 16)

    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: Page.Gui) : PageComponent.Gui(parent) {
        override fun draw(mouse: Vec2d, time: Double) {
            parent.gui.drawStack(stack, drawPos)
        }

        override fun postDraw(mouse: Vec2d, time: Double) {
            if (mouse in drawPos to (drawPos + size)) {
                parent.gui.renderToolTip(stack, mouse)
            }
        }
    }
}