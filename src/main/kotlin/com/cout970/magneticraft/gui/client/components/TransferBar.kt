package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 11/07/2016.
 */
class TransferBar(val value: () -> Double, val min: () -> Double, val base: () -> Double, val max: () -> Double, pos: Vec2d) :
        CompVerticalBar(EmptyBarProvider, 4, pos), IBarProvider {

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.run {
            bindTexture(BAR_TEXTURES)
            var level = Math.round(getLevel() * 24).toInt()
            if (level > 0) {
                drawScaledTexture(Vec2d(pos.x, pos.yi - level - 24), Vec2d(5, level), Vec2d(index * 5, 24 - level), Vec2d(64, 64))
            } else if (level < 0) {
                level = -level
                drawScaledTexture(Vec2d(pos.x, pos.yi - 23), Vec2d(5, level - 1), Vec2d(index * 5, 49 - level), Vec2d(64, 64))
            }
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in box) {
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

private object EmptyBarProvider : IBarProvider {
    override fun getLevel(): Float = 0f
}