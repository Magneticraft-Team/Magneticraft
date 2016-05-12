package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page
import net.minecraft.util.ResourceLocation

class Image(
    position: Coords,
    override val size: Coords,
    val location: ResourceLocation
) : PageComponent(position) {
    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: Page.Gui) : PageComponent.Gui(parent) {
        override fun draw(mouse: Coords, time: Double) {
            parent.gui.drawTexture(drawPos, size, location)
        }
    }
}