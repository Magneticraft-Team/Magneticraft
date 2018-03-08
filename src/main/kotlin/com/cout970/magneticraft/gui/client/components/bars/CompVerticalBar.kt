package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.client.renderer.GlStateManager

/**
 * Created by cout970 on 09/07/2016.
 */

val BAR_TEXTURES = resource("textures/gui/bar_textures.png")

open class CompVerticalBar(
        val provider: IBarProvider,
        val index: Int,
        override val pos: Vec2d,
        val tooltip: () -> List<String> = { listOf<String>() }
) : IComponent {

    override val size = Vec2d(5, 48)

    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(BAR_TEXTURES)
        val level = (provider.getLevel() * 48).toInt()

        GlStateManager.color(1f, 1f, 1f, 0.2f)
        gui.drawTexture(DrawableBox(
                screen = Pair(gui.pos + pos, vec2Of(5, 48)),
                texture = vec2Of(index * 5, 0) to vec2Of(5, 48),
                textureSize = vec2Of(64, 64)
        ))
        GlStateManager.color(1f, 1f, 1f, 1f)

        gui.drawTexture(DrawableBox(
                screen = Pair(gui.pos + pos + vec2Of(0, 48 - level), vec2Of(5, level)),
                texture = vec2Of(index * 5, 48 - level) to vec2Of(5, level),
                textureSize = vec2Of(64, 64)
        ))

//        gui.drawColor(gui.pos + pos to size, 0x7FFF7070.toInt())
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val text = tooltip.invoke()
            if (!text.isEmpty()) {
                gui.drawHoveringText(text, mouse)
            }
        }
    }
}