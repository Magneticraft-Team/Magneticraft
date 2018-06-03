package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 08/07/2016.
 */
class CompBackground(
        val texture: ResourceLocation,
        val textureSize: Vec2d = Vec2d(256, 256),
        override val size: Vec2d = Vec2d(176, 166)
) : IComponent {

    override val pos: IVector2 = Vec2d.ZERO

    override lateinit var gui: IGui
    lateinit var back: DrawableBox

    override fun init() {
        back = DrawableBox(gui.pos, size, Vec2d.ZERO, size, textureSize)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        gui.drawTexture(DrawableBox(gui.pos, size, Vec2d.ZERO, size, textureSize))
    }
}

class CompDynamicBackground(
        override val size: Vec2d = Vec2d(176, 166)
) : IComponent {

    override val pos: IVector2 = Vec2d.ZERO

    override lateinit var gui: IGui

    lateinit var back: DrawableBox
    lateinit var dynamicBack: List<DrawableBox>

    override fun init() {
        back = DrawableBox(gui.pos, size, Vec2d.ZERO, size, Vec2d(256, 256))

        val pos = gui.components.filter { it != this }.map { it.pos }
        val size = gui.components.filter { it != this }.map { it.pos + it.size }

        val min = vec2Of(
                pos.map { it.xi }.min() ?: 0,
                pos.map { it.yi }.min() ?: 0
        ) - vec2Of(7, 7)

        val max = vec2Of(
                size.map { it.xi }.max() ?: 0,
                size.map { it.yi }.max() ?: 0
        ) + vec2Of(7, 7)

        dynamicBack = createTextureBox(gui.pos + min, max - min)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(guiTexture("base"))
        gui.drawTexture(back)
        gui.bindTexture(guiTexture("misc"))
        dynamicBack.forEach { gui.drawTexture(it) }
    }

    fun createTextureBox(pPos: IVector2, pSize: IVector2): List<DrawableBox> {
        val texSize = vec2Of(256, 256)

        val leftUp = DrawableBox(
                screenPos = pPos,
                screenSize = vec2Of(4),
                texturePos = vec2Of(0),
                textureSize = vec2Of(4),
                textureScale = texSize
        )

        val leftDown = DrawableBox(
                screenPos = pPos + vec2Of(0, pSize.yi - 4),
                screenSize = vec2Of(4),
                texturePos = vec2Of(0, 5),
                textureSize = vec2Of(4),
                textureScale = texSize
        )

        val rightUp = DrawableBox(
                screenPos = pPos + vec2Of(pSize.xi - 4, 0),
                screenSize = vec2Of(4),
                texturePos = vec2Of(5, 0),
                textureSize = vec2Of(4),
                textureScale = texSize
        )

        val rightDown = DrawableBox(
                screenPos = pPos + vec2Of(pSize.xi - 4, pSize.yi - 4),
                screenSize = vec2Of(4),
                texturePos = vec2Of(5, 5),
                textureSize = vec2Of(4),
                textureScale = texSize
        )

        val left = DrawableBox(
                screenPos = pPos + vec2Of(0, 4),
                screenSize = vec2Of(4, pSize.y - 8),
                texturePos = vec2Of(0, 10),
                textureSize = vec2Of(4, pSize.y - 8),
                textureScale = texSize
        )

        val right = DrawableBox(
                screenPos = pPos + vec2Of(pSize.xi - 4, 4),
                screenSize = vec2Of(4, pSize.yi - 8),
                texturePos = vec2Of(5, 10),
                textureSize = vec2Of(4, pSize.yi - 8),
                textureScale = texSize
        )

        val up = DrawableBox(
                screenPos = pPos + vec2Of(4, 0),
                screenSize = vec2Of(pSize.xi - 8, 4),
                texturePos = vec2Of(10, 0),
                textureSize = vec2Of(pSize.xi - 8, 4),
                textureScale = texSize
        )

        val down = DrawableBox(
                screenPos = pPos + vec2Of(4, pSize.yi - 4),
                screenSize = vec2Of(pSize.xi - 8, 4),
                texturePos = vec2Of(10, 5),
                textureSize = vec2Of(pSize.xi - 8, 4),
                textureScale = texSize
        )

        val center = DrawableBox(
                screenPos = pPos + vec2Of(4, 4),
                screenSize = vec2Of(pSize.xi - 8, pSize.yi - 8),
                texturePos = vec2Of(5, 3),
                textureSize = vec2Of(1, 1),
                textureScale = texSize
        )

        return listOf(
                leftUp, leftDown, rightUp, rightDown,
                left, right, up, down, center
        )
    }
}