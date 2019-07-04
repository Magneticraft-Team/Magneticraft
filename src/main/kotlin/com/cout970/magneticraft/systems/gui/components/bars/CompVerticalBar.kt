package com.cout970.magneticraft.systems.gui.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.clamp
import com.cout970.magneticraft.misc.crafting.TimedCraftingProcess
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.renderer.GlStateManager.*
import kotlin.math.roundToInt

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

        enableBlend()
        enableAlpha()
        color(1f, 1f, 1f, 0.2f)
        gui.drawTexture(DrawableBox(
            gui.pos + pos, vec2Of(5, 48),
            vec2Of(index * 5, 0), vec2Of(5, 48),
            vec2Of(64, 64)
        ))
        color(1f, 1f, 1f, 1f)

        gui.drawTexture(DrawableBox(
            gui.pos + pos + vec2Of(0, 48 - level), vec2Of(5, level),
            vec2Of(index * 5, 48 - level), vec2Of(5, level),
            vec2Of(64, 64)
        ))
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

open class CompDynamicBar(
    val provider: IBarProvider,
    val index: Int,
    override val pos: Vec2d,
    val tooltip: () -> List<String> = { emptyList() }
) : IComponent {

    override val size = Vec2d(7, 50)

    override lateinit var gui: IGui
    lateinit var back: DrawableBox
    lateinit var color: DrawableBox

    constructor(pos: Vec2d, index: Int, va: ValueAverage, limit: Number, tooltip: (CallbackBarProvider) -> (() -> List<String>))
        : this(va.toBarProvider(limit), index, pos, tooltip(va.toBarProvider(limit)))

    constructor(pos: Vec2d, index: Int, timed: TimedCraftingProcess, tooltip: (CallbackBarProvider) -> (() -> List<String>))
        : this(timed.toBarProvider(), index, pos, tooltip(timed.toBarProvider()))

    override fun init() {
        back = DrawableBox(
            screenPos = gui.pos + pos,
            screenSize = vec2Of(7, 50),
            texturePos = vec2Of(10, 10),
            textureSize = vec2Of(7, 50),
            textureScale = vec2Of(256)
        )
        color = DrawableBox(
            screenPos = gui.pos + pos + vec2Of(1),
            screenSize = vec2Of(5, 48),
            texturePos = vec2Of(22 + index * 5, 10),
            textureSize = vec2Of(5, 48),
            textureScale = vec2Of(256)
        )
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val level = (provider.getLevel() * 48).toInt()
        val front = DrawableBox(
            screenPos = gui.pos + pos + vec2Of(1, 1 + 48 - level),
            screenSize = vec2Of(5, level),
            texturePos = vec2Of(22 + index * 5, 10 + 48 - level),
            textureSize = vec2Of(5, level),
            textureScale = vec2Of(256)
        )

        color(1f, 1f, 1f, 1f)
        enableBlend()
        gui.bindTexture(guiTexture("misc"))
        back.draw()

        color(1f, 1f, 1f, 0.2f)
        color.draw()

        color(1f, 1f, 1f, 1f)
        front.draw()
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val text = tooltip.invoke()
            if (text.isNotEmpty()) {
                gui.drawHoveringText(text, mouse)
            }
        }
    }
}


open class GuiValueBar(
    override val pos: IVector2,
    override val size: IVector2 = Vec2d(12, 54),
    val backgroundTextureOffset: IVector2 = vec2Of(10, 120),
    val colorTextureOffset: IVector2 = vec2Of(23, 121),
    val value: () -> Double,
    val tooltip: () -> List<String> = { emptyList() }
) : IComponent {

    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val border = vec2Of(1)
        val start = gui.pos + pos
        val colorSize = size - border * 2
        val rawValue = value()


        val value = if (rawValue.isNaN() || rawValue.isInfinite()) 0 else {
            (clamp(rawValue, 1.0, 0.0) * colorSize.y).roundToInt()
        }

        color(1f, 1f, 1f, 1f)
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(
            screenPos = start,
            screenSize = size,
            texturePos = backgroundTextureOffset
        )

        enableAlpha()
        enableBlend()
        color(1f, 1f, 1f, 0.2f)
        gui.drawTexture(
            screenPos = start + border,
            screenSize = colorSize,
            texturePos = colorTextureOffset
        )
        color(1f, 1f, 1f, 1f)
        disableBlend()
        //  Colored bar
        gui.drawTexture(
            screenPos = start + border + vec2Of(0, colorSize.yi - value),
            screenSize = colorSize.withY(value),
            texturePos = colorTextureOffset + vec2Of(0, colorSize.yi - value)
        )
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val text = tooltip.invoke()
            if (text.isNotEmpty()) {
                gui.drawHoveringText(text, mouse)
            }
        }
    }
}

data class CompDrawable(
    override val pos: IVector2,
    override val size: IVector2,
    val texture: IVector2,
    val enable: () -> Boolean = { true }
) : IComponent {

    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        if (!enable()) return

        color(1f, 1f, 1f, 1f)
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(
            screenPos = gui.pos + pos,
            screenSize = size,
            texturePos = texture
        )
    }
}