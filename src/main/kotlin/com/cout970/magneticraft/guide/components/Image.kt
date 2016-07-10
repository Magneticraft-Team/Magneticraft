package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

class Image(
    position: Vec2d,
    override val size: Vec2d,
    val location: ResourceLocation
) : PageComponent(position) {
    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: Page.Gui) : PageComponent.Gui(parent) {
        override fun draw(mouse: Vec2d, time: Double) {
            parent.gui.drawTexture(drawPos, size, location)
        }
    }
}