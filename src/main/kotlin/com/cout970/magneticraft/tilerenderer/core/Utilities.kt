package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14

/**
 * Created by cout970 on 2017/06/16.
 */

const val PIXEL = 0.0625

object Utilities {

    val WIRE_TEXTURE = resource("textures/models/wire_texture.png")


//    fun renderMultiblockBlueprint(multiblock: Multiblock) {
//        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
//        for (j in 0 until multiblock.size.y) {
//            for (i in 0 until multiblock.size.x) {
//                for (k in 0 until multiblock.size.z) {
//                    val component = multiblock.scheme[i, j, k]
//                    val blocks = component.getBlueprintBlocks(multiblock, BlockPos(i, j, k))
//                    for (stack in blocks) {
//                        stack.item ?: continue
//                        pushMatrix()
//                        translate(PIXEL * 8, PIXEL * 5, PIXEL * 5)
//                        val pos = vec3Of(i, j, k) - multiblock.center.toVec3d()
//                        translate(pos.xd, pos.yd, pos.zd)
//                        if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack)) {
//                            translate(0.0, -0.045, 0.125)
//                            rotate(90f, 1f, 0f, 0f)
//                        } else {
//                            translate(0.0, -0.125, 0.0625 * 3)
//                        }
//                        scale(2.0, 2.0, 2.0)
//                        renderItemWithTransparency(stack, ItemCameraTransforms.TransformType.GROUND, 0.5f)
//                        popMatrix()
//                    }
//                }
//            }
//        }
//    }

    /**
     * This uses DOWN as default facing
     */
    fun rotateAroundCenter(facing: EnumFacing){
        when (facing.opposite) {
            EnumFacing.UP -> {
                Utilities.customRotate(vec3Of(180, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.NORTH -> {
                Utilities.customRotate(vec3Of(90, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.SOUTH -> {
                Utilities.customRotate(vec3Of(-90, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.WEST -> {
                Utilities.customRotate(vec3Of(0, 0, -90), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.EAST -> {
                Utilities.customRotate(vec3Of(0, 0, 90), Vec3d(0.5, 0.5, 0.5))
            }
            else -> Unit
        }
    }

    fun renderItemWithTransparency(stack: ItemStack, transform: ItemCameraTransforms.TransformType, alpha: Float) {
        var bakedmodel = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(stack, null, null)
        if (stack.item != null) {
            val textureManager = Minecraft.getMinecraft().textureManager
            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false)
            enableRescaleNormal()
            alphaFunc(516, 0.1f)
            color(1.0f, 1.0f, 1.0f, 1.0f)
            enableBlend()
            GL14.glBlendColor(1f, 1f, 1f, alpha)
            glBlendFunc(GL_CONSTANT_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA)
            pushMatrix()

            bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, transform, false)

            Minecraft.getMinecraft().renderItem.renderItem(stack, bakedmodel)
            cullFace(CullFace.BACK)
            popMatrix()
            disableRescaleNormal()
            disableBlend()
            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap()
        }
    }

    fun customRotate(rot: Vec3d, pos: Vec3d) {
        translate(pos.xd, pos.yd, pos.zd)
        rotate(rot.xf, 1f, 0f, 0f)
        rotate(rot.yf, 0f, 1f, 0f)
        rotate(rot.zf, 0f, 0f, 1f)
        translate(-pos.xd, -pos.yd, -pos.zd)
    }

    fun renderBox(box: AxisAlignedBB, color: IVector3 = vec3Of(1,1,1)) {
        val tes = Tessellator.getInstance()
        val t = tes.buffer
        val r = color.xf
        val g = color.yf
        val b = color.zf
        val a = 1f

        glDisable(GL_TEXTURE_2D)
        GlStateManager.glLineWidth(2f)
        t.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        t.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex()
        t.pos(box.maxX, box.minY, box.minZ).color(r, g, b, a).endVertex()

        t.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex()
        t.pos(box.minX, box.maxY, box.minZ).color(r, g, b, a).endVertex()

        t.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex()
        t.pos(box.minX, box.minY, box.maxZ).color(r, g, b, a).endVertex()

        t.pos(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).endVertex()
        t.pos(box.minX, box.maxY, box.maxZ).color(r, g, b, a).endVertex()

        t.pos(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).endVertex()
        t.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, a).endVertex()

        t.pos(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).endVertex()
        t.pos(box.maxX, box.maxY, box.minZ).color(r, g, b, a).endVertex()

        t.pos(box.minX, box.maxY, box.minZ).color(r, g, b, a).endVertex()
        t.pos(box.maxX, box.maxY, box.minZ).color(r, g, b, a).endVertex()

        t.pos(box.maxX, box.minY, box.minZ).color(r, g, b, a).endVertex()
        t.pos(box.maxX, box.maxY, box.minZ).color(r, g, b, a).endVertex()

        t.pos(box.minX, box.maxY, box.minZ).color(r, g, b, a).endVertex()
        t.pos(box.minX, box.maxY, box.maxZ).color(r, g, b, a).endVertex()

        t.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, a).endVertex()
        t.pos(box.minX, box.minY, box.maxZ).color(r, g, b, a).endVertex()

        t.pos(box.minX, box.maxY, box.maxZ).color(r, g, b, a).endVertex()
        t.pos(box.minX, box.minY, box.maxZ).color(r, g, b, a).endVertex()

        t.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, a).endVertex()
        t.pos(box.maxX, box.minY, box.minZ).color(r, g, b, a).endVertex()

        tes.draw()
        glEnable(GL_TEXTURE_2D)
    }

    fun renderLine(pos: IVector3, end: IVector3, color: IVector3 = vec3Of(1,1,1)) {
        val tes = Tessellator.getInstance()
        val t = tes.buffer
        val r = color.xf
        val g = color.yf
        val b = color.zf
        val a = 1.0f

        glDisable(GL_TEXTURE_2D)
        GlStateManager.glLineWidth(2f)
        t.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        t.pos(pos.xd, pos.yd, pos.zd).color(r, g, b, a).endVertex()
        t.pos(end.xd, end.yd, end.zd).color(r, g, b, a).endVertex()
        tes.draw()
        glEnable(GL_TEXTURE_2D)
    }

    fun rotateFromCenter(facing: EnumFacing, optional: Float = 0f) {
        val angle = when (facing) {
                        EnumFacing.NORTH -> 0f
                        EnumFacing.SOUTH -> 180f
                        EnumFacing.WEST -> 90f
                        EnumFacing.EAST -> -90f
                        else -> 0f
                    } + optional
        translate(0.5, 0.5, 0.5)
        rotate(angle, 0f, 1f, 0f)
        translate(-0.5, -0.5, -0.5)
    }

//    fun renderMultiblockBoundingBoxes(te: TileMultiblock){
//        val global = te.multiblock!!.getGlobalCollisionBox().map {
//            te.multiblockFacing!!.rotateBox(vec3Of(0.5, 0.5, 0.5), it)
//        }
//        global.forEach(::renderBox)
//    }

    fun renderFloatingLabel(str: String, pos: Vec3d) {

        val (x, y, z) = pos
        val renderManager = Minecraft.getMinecraft().renderManager
        val fontrenderer = renderManager.fontRenderer
        val f = 1.6f
        val f1 = 0.016666668f * f
        pushMatrix()
        translate(x.toFloat() + 0.0f, y.toFloat() + 0.5f, z.toFloat())
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
        rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        rotate(renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        scale(-f1, -f1, f1)
        disableLighting()
        depthMask(false)
        disableDepth()
        enableBlend()
        tryBlendFuncSeparate(770, 771, 1, 0)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.buffer
        val i = 0

        val j = fontrenderer.getStringWidth(str) / 2
        disableTexture2D()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
        worldrenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        tessellator.draw()
        enableTexture2D()
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127)
        enableDepth()
        depthMask(true)
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1)
        enableLighting()
        disableBlend()
        color(1.0f, 1.0f, 1.0f, 1.0f)
        popMatrix()
    }

    fun drawLine(t: BufferBuilder, a: Vec3d, b: Vec3d) {
        val w = 0.0625 / 2
        t.pos(a.xd, a.yd - w, a.zd).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(a.xd, a.yd + w, a.zd).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd + w, b.zd).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd - w, b.zd).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

        t.pos(a.xd, a.yd, a.zd - w).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(a.xd, a.yd, a.zd + w).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd, b.zd + w).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd, b.zd - w).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

        t.pos(a.xd - w, a.yd, a.zd).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(a.xd + w, a.yd, a.zd).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd + w, b.yd, b.zd).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd - w, b.yd, b.zd).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
        //inverted
        t.pos(a.xd, a.yd + w, a.zd).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(a.xd, a.yd - w, a.zd).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd - w, b.zd).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd + w, b.zd).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

        t.pos(a.xd, a.yd, a.zd + w).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(a.xd, a.yd, a.zd - w).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd, b.zd - w).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd, b.yd, b.zd + w).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()

        t.pos(a.xd + w, a.yd, a.zd).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(a.xd - w, a.yd, a.zd).tex(0.125, 0.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd - w, b.yd, b.zd).tex(0.125, 1.0).normal(0f, 1f, 0f).endVertex()
        t.pos(b.xd + w, b.yd, b.zd).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
    }

    fun renderConnection(con: IElectricConnection, a: IWireConnector, b: IWireConnector, weight: Double = 0.05) {
        val origins = a.connectors
        val destinations = b.connectors
        val direction = b.pos.subtract(a.pos)

        if (origins.size != destinations.size) return
        for (c in origins.indices) {
            val order = b.getConnectorIndex(c, a, con)
            val start = origins[c]
            val end = direction.toVec3d().add(destinations[order])

            val tes = Tessellator.getInstance()
            val buffer = tes.buffer

            buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)

            val points = interpolateWire(start, end, weight)

            for (p in 0..points.size - 2) {
                drawLine(buffer, points[p], points[p + 1])
            }
            tes.draw()
        }
    }

    fun interpolateWire(start: Vec3d, end: Vec3d, mass: Double): List<Vec3d> {
        val list = mutableListOf<Vec3d>()
        val distance = start.distanceTo(end)
        val middle = Vec3d(
                (start.xd + end.xd) / 2,
                (start.yd + end.yd) / 2 - distance * mass,
                (start.zd + end.zd) / 2)

        for (i in 0..10) {
            val p = i / 10.0
            val x = interpolate(start.xd, middle.xd, end.xd, p)
            val y = interpolate(start.yd, middle.yd, end.yd, p)
            val z = interpolate(start.zd, middle.zd, end.zd, p)
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

    fun interpolate(fa: Double, fb: Double, x: Double): Double {
        return fa + (fb - fa) * x
    }

    fun interpolate(fa: Float, fb: Float, x: Float): Float {
        return fa + (fb - fa) * x
    }
}