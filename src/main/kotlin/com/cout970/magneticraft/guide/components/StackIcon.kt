package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.BookPage
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import net.minecraft.item.ItemStack

class StackIcon(
        position: Vec2d,
        val stack: ItemStack
) : PageComponent(position) {

    override val id: String = "stack"

    override val size = Vec2d(16, 16)

    override fun toGuiComponent(parent: BookPage.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: BookPage.Gui) : PageComponent.Gui(parent) {
        override fun draw(mouse: Vec2d, time: Double) {
            parent.gui.drawStack(stack, drawPos)
        }

        override fun postDraw(mouse: Vec2d, time: Double) {
            if (mouse in drawPos toPoint (drawPos + size)) {
                parent.gui.renderToolTip(stack, mouse)
            }
        }
    }
}