package com.cout970.magneticraft.systems.gui.components.bars

import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 11/07/2016.
 */
class TransferRateBar(
    val value: () -> Double,
    val min: () -> Double,
    val base: () -> Double,
    val max: () -> Double,
    pos: Vec2d
) :
    CompVerticalBar(EmptyBarProvider, 4, pos), IBarProvider {

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(
            gui.pos + pos - vec2Of(1),
            vec2Of(7, 50),
            vec2Of(10, 10)
        )

        enableAlpha()
        enableBlend()
        color(1f,1f,1f, 0.2f)
        gui.drawTexture(
            gui.pos + pos,
            vec2Of(5, 48),
            vec2Of(42, 10)
        )
        color(1f,1f,1f, 1f)

        gui.bindTexture(BAR_TEXTURES)
        var level = Math.round(getLevel() * 24)
        if (level > 0) {
            gui.drawTexture(
                gui.pos + pos + vec2Of(0, 48 - level - 24),
                vec2Of(5, level),
                vec2Of(index * 5, 24 - level),
                vec2Of(5, level),
                vec2Of(64, 64)
            )
        } else if (level < 0) {
            level = -level
            gui.drawTexture(
                gui.pos + pos + vec2Of(0, 48 - 23),
                vec2Of(5, level - 1),
                vec2Of(index * 5, 49 - level),
                vec2Of(5, level - 1),
                vec2Of(64, 64)
            )
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val color = when {
                value.invoke() > 0 -> TextFormatting.DARK_GREEN.toString() + "+"
                value.invoke() < 0 -> TextFormatting.DARK_RED.toString()
                else -> TextFormatting.WHITE.toString()
            }
            val list = listOf(String.format("%s%.2fW", color, value.invoke()))
            gui.drawHoveringText(list, mouse)
        }
    }

    override fun getLevel(): Float {
        return when {
            value() < min() -> -1f
            value() > max() -> 1f
            value() < base() -> -1 + ((value() - min()) / (base() - min())).toFloat()
            else -> ((value() - base()) / (max() - base())).toFloat()
        }
    }
}

