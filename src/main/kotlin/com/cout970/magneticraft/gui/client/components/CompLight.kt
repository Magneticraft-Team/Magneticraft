package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 10/07/2016.
 */

class CompLight(
    val on: DrawableBox,
    val off: DrawableBox,
    val texture: ResourceLocation,
    val condition: () -> Boolean) : IComponent {

    override lateinit var gui: IGui
    override val pos: IVector2 = on.screenPos
    override val size: IVector2 = on.screenSize

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        if (condition()) {
            gui.drawTexture(on)
        } else {
            gui.drawTexture(off)
        }
    }
}