package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 11/07/2016.
 */
class TransferRateBar(val value: () -> Double, val min: () -> Double, val base: () -> Double, val max: () -> Double,
                      pos: Vec2d) :
    CompVerticalBar(EmptyBarProvider, 4, pos), IBarProvider {

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(BAR_TEXTURES)
        var level = Math.round(getLevel() * 24)
        if (level > 0) {
            gui.drawTexture(DrawableBox(
                gui.pos + pos + vec2Of(0, 48 - level - 24), vec2Of(5, level),
                vec2Of(index * 5, 24 - level), vec2Of(5, level),
                vec2Of(64, 64)
            ))
        } else if (level < 0) {
            level = -level
            gui.drawTexture(DrawableBox(
                gui.pos + pos + vec2Of(0, 48 - 23), vec2Of(5, level - 1),
                vec2Of(index * 5, 49 - level), vec2Of(5, level - 1),
                vec2Of(64, 64)
            ))
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val color = if (value.invoke() > 0) {
                TextFormatting.DARK_GREEN.toString() + "+"
            } else if (value.invoke() < 0) {
                TextFormatting.DARK_RED.toString()
            } else {
                TextFormatting.WHITE.toString()
            }
            val list = listOf(String.format("%s%.2fW", color, value.invoke()))
            gui.drawHoveringText(list, mouse)
        }
    }

    override fun getLevel(): Float {
        if (value.invoke() < min.invoke()) return -1f
        if (value.invoke() > max.invoke()) return 1f
        if (value.invoke() < base.invoke()) {
            return -1 + ((value.invoke() - min.invoke()) / (base.invoke() - min.invoke())).toFloat()
        } else {
            return ((value.invoke() - base.invoke()) / (max.invoke() - base.invoke())).toFloat()
        }
    }
}

