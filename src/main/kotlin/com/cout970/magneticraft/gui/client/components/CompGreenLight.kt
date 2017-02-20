package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.IComponent
import com.cout970.magneticraft.gui.client.IGui
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 10/07/2016.
 */

val MISC_TEXTURES = resource("textures/gui/misc_textures.png")

class CompGreenLight(val pos: Vec2d, val condition: () -> Boolean) : IComponent {

    override val box: Box = Box(pos, Vec2d(7, 7))
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.run {
            bindTexture(MISC_TEXTURES)
            if (condition.invoke()) {
                drawScaledTexture(this@CompGreenLight.box, Vec2d(7, 57), Vec2d(64, 64))
            } else {
                drawScaledTexture(this@CompGreenLight.box, Vec2d(0, 57), Vec2d(64, 64))
            }
        }
    }
}