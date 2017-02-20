package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.IComponent
import com.cout970.magneticraft.gui.client.IGui
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.clamp
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 09/07/2016.
 */

val BAR_TEXTURES = resource("textures/gui/bar_textures.png")

open class CompVerticalBar(val provider: IBarProvider, val index: Int, val pos: Vec2d, val tooltip: () -> List<String> = { listOf<String>() }) : IComponent {

    val size = Vec2d(5, 48)
    override val box: Box = Box(pos.copy(y = pos.y - size.y), size)
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.run {
            bindTexture(BAR_TEXTURES)
            val level = (provider.getLevel() * 48).toInt()
            drawScaledTexture(Vec2d(pos.x, pos.yi - level), Vec2d(5, level), Vec2d(index * 5, 48 - level), Vec2d(64, 64))
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in box) {
            val text = tooltip.invoke()
            if (!text.isEmpty()) {
                gui.drawHoveringText(text, mouse)
            }
        }
    }
}

interface IBarProvider {
    fun getLevel(): Float
}

open class CallbackBarProvider(val callback: () -> Double, val max: () -> Double, val min: () -> Double) : IBarProvider {

    override fun getLevel(): Float = clamp((callback.invoke() - min.invoke()) / (max.invoke() - min.invoke()), 1.0, 0.0).toFloat()
}