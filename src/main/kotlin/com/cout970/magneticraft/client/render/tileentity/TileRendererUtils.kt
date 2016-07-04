package com.cout970.magneticraft.client.render.tileentity

import coffee.cypher.mcextlib.extensions.vectors.component1
import coffee.cypher.mcextlib.extensions.vectors.component2
import coffee.cypher.mcextlib.extensions.vectors.component3
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 29/06/2016.
 */

fun renderFloatingLabel(str: String, pos: Vec3d) {

    val (x,y,z) = pos
    val renderManager = Minecraft.getMinecraft().renderManager
    val fontrenderer = renderManager.fontRenderer
    val f = 1.6f
    val f1 = 0.016666668f * f
    GlStateManager.pushMatrix()
    GlStateManager.translate(x.toFloat() + 0.0f, y.toFloat() + 0.5f, z.toFloat())
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
    GlStateManager.scale(-f1, -f1, f1)
    GlStateManager.disableLighting()
    GlStateManager.depthMask(false)
    GlStateManager.disableDepth()
    GlStateManager.enableBlend()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    val tessellator = Tessellator.getInstance()
    val worldrenderer = tessellator.buffer
    val i = 0

    val j = fontrenderer.getStringWidth(str) / 2
    GlStateManager.disableTexture2D()
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
    worldrenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    worldrenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    worldrenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    worldrenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    tessellator.draw()
    GlStateManager.enableTexture2D()
    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127)
    GlStateManager.enableDepth()
    GlStateManager.depthMask(true)
    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1)
    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
}

fun drawLine(t: VertexBuffer, a: Vec3d, b: Vec3d) {
    val w = 0.0625 / 2
    t.pos(a.xCoord, a.yCoord - w, a.zCoord).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(a.xCoord, a.yCoord + w, a.zCoord).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord + w, b.zCoord).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord - w, b.zCoord).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

    t.pos(a.xCoord, a.yCoord, a.zCoord - w).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(a.xCoord, a.yCoord, a.zCoord + w).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord, b.zCoord + w).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord, b.zCoord - w).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

    t.pos(a.xCoord - w, a.yCoord, a.zCoord).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(a.xCoord + w, a.yCoord, a.zCoord).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord + w, b.yCoord, b.zCoord).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord - w, b.yCoord, b.zCoord).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
    //inverted
    t.pos(a.xCoord, a.yCoord + w, a.zCoord).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(a.xCoord, a.yCoord - w, a.zCoord).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord - w, b.zCoord).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord + w, b.zCoord).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

    t.pos(a.xCoord, a.yCoord, a.zCoord + w).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(a.xCoord, a.yCoord, a.zCoord - w).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord, b.zCoord - w).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord, b.yCoord, b.zCoord + w).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

    t.pos(a.xCoord + w, a.yCoord, a.zCoord).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(a.xCoord - w, a.yCoord, a.zCoord).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord - w, b.yCoord, b.zCoord).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
    t.pos(b.xCoord + w, b.yCoord, b.zCoord).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
}

fun interpolateWire(start: Vec3d, end: Vec3d): List<Vec3d> {
    val list = mutableListOf<Vec3d>()
    val distance = start.distanceTo(end)
    val middle = Vec3d(
            (start.xCoord + end.xCoord) / 2,
            (start.yCoord + end.yCoord) / 2 - distance * 0.05,
            (start.zCoord + end.zCoord) / 2)

    for (i in 0..10) {
        val p = i / 10.0
        val x = interpolate(start.xCoord, middle.xCoord, end.xCoord, p)
        val y = interpolate(start.yCoord, middle.yCoord, end.yCoord, p)
        val z = interpolate(start.zCoord, middle.zCoord, end.zCoord, p)
        list.add(Vec3d(x, y, z))
    }
    return list
}

fun interpolate(fa: Double, fb: Double, fc: Double, x: Double): Double {
    val a = 0.0
    val b = 0.5
    val c = 1.0
    val L0 = (x - b) / (a - b) * ((x - c) / (a - c))
    val L1 = (x - a) / (b - a) * ((x - c) / (b - c))
    val L2 = (x - a) / (c - a) * ((x - b) / (c - b))
    return fa * L0 + fb * L1 + fc * L2
}