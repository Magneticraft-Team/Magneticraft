package com.cout970.magneticraft.misc.render

import com.mojang.blaze3d.platform.GlStateManager
import org.lwjgl.opengl.GL11

@Suppress("NOTHING_TO_INLINE")
object GL {
    inline fun translate(x: Number, y: Number, z: Number) = GlStateManager.translatef(x.toFloat(), y.toFloat(), z.toFloat())

    inline fun rotate(angle: Number, x: Number, y: Number, z: Number) = GlStateManager.rotatef(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())

    inline fun scale(x: Number, y: Number, z: Number) = GlStateManager.scalef(x.toFloat(), y.toFloat(), z.toFloat())

    inline fun color(r: Number, g: Number = r, b: Number = r, a: Number = 1f) = GlStateManager.color4f(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())

    inline fun pushMatrix() = GL11.glPushMatrix()

    inline fun popMatrix() = GL11.glPopMatrix()
}