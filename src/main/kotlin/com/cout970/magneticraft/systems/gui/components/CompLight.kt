package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
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