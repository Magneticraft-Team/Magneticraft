package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import com.cout970.magneticraft.systems.gui.render.isCtrlKeyDown
import net.minecraft.client.renderer.GlStateManager.color
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
        gui.drawTexture(back)
    }
}

class CompDebugOutline(
    override val pos: IVector2,
    override val size: IVector2
) : IComponent {

    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {}

    override fun drawSecondLayer(mouse: Vec2d) {
        if (!isCtrlKeyDown()) return
        color(1f, 1f, 1f, 1f)
        val start = pos
        val end = start + size

        gui.drawColor(start, end, 0xFF000000.toInt())
        gui.drawColor(start + 1, end - 1, -1)
    }
}

class CompDynamicBackground(
    override val pos: IVector2,
    override val size: IVector2
) : IComponent {

    override lateinit var gui: IGui

    lateinit var dynamicBack: List<DrawableBox>

    override fun init() {
        dynamicBack = createTextureBox(gui.pos + pos, size)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        color(1f, 1f, 1f, 1f)
        gui.bindTexture(guiTexture("misc2"))
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