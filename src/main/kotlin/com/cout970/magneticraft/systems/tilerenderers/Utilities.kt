package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.misc.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.split
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.blocks.BlockMultiblock
import com.cout970.magneticraft.systems.multiblocks.IMultiblockModule
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.multiblocks.MultiblockContext
import com.cout970.magneticraft.systems.multiblocks.MultiblockManager
import com.cout970.magneticraft.systems.tilemodules.ModuleMultiblockIO
import com.cout970.vector.extensions.Vector3
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemSkull
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14
import kotlin.math.max

/**
 * Created by cout970 on 2017/06/16.
 */

const val PIXEL = 0.0625

private val BREAK_LINES_REGEX = """(\\n)|(\n)""".toRegex()

@Suppress("NOTHING_TO_INLINE")
inline fun modelOf(block: Block, id: String = "model") = ModelResourceLocation(block.registryName!!, id)

object Utilities {

    val WIRE_TEXTURE = resource("textures/models/wire_texture.png")

    fun renderMultiblockBlueprint(ctx: MultiblockContext) {

        GlStateManager.disableDepth()
        val mb: Multiblock = ctx.multiblock
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        for (j in 0 until mb.size.y) {
            for (i in 0 until mb.size.x) {
                for (k in 0 until mb.size.z) {
                    val component = mb.scheme[i, j, k]
                    val relPos = BlockPos(i, j, k)
                    val blockPos = MultiblockManager.applyFacing(ctx, relPos)

                    if (component.checkBlock(blockPos, ctx).isEmpty()) {
                        continue
                    }

                    val blocks = component.getBlueprintBlocks(mb, relPos)
                    val stack = blocks.firstOrNull() ?: continue

                    pushMatrix()
                    translate(PIXEL * 8, PIXEL * 5, PIXEL * 5)
                    val pos = vec3Of(i, j, k) - mb.center.toVec3d()
                    translate(pos.xd, pos.yd, pos.zd)

                    if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack)) {
                        translate(0.0, -0.045, 0.125)
                        rotate(90f, 1f, 0f, 0f)
                    } else {
                        translate(0.0, -0.125, 0.0625 * 3)
                    }

                    scale(2.0, 2.0, 2.0)
                    if (Minecraft.getMinecraft().world.isAirBlock(ctx.center.plus(blockPos))) enableDepth()
                    renderItemWithTransparency(stack, ItemCameraTransforms.TransformType.GROUND, 0.6f)
                    disableDepth()
                    popMatrix()
                }
            }
        }
        GlStateManager.enableDepth()
    }

    fun renderMultiblockHitboxes(facing: EnumFacing, multiblock: Multiblock) {
        multiblock.getGlobalCollisionBoxes().map {
            val origin = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it)
            facing.rotateBox(vec3Of(0.5), origin)
        }.forEach { renderBox(it) }
    }

    fun rotationScaled(scale: Float): Float {
        val value = (System.currentTimeMillis() and 0xFFFF).toFloat() / 0xFFFF.toFloat()
        return value * scale
    }

    /**
     * This uses DOWN as default facing
     */
    fun rotateAroundCenter(facing: EnumFacing) {
        when (facing.opposite) {
            EnumFacing.UP -> {
                customRotate(vec3Of(180, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.NORTH -> {
                customRotate(vec3Of(0, 90, 90), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.SOUTH -> {
                customRotate(vec3Of(0, -90, 90), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.WEST -> {
                customRotate(vec3Of(0, 0, -90), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.EAST -> {
                customRotate(vec3Of(0, 0, 90), Vec3d(0.5, 0.5, 0.5))
            }
            else -> Unit
        }
    }

    fun renderItem(stack: ItemStack, facing: EnumFacing = EnumFacing.NORTH) {
        if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
            translate(0.0, -0.9 * PIXEL, 0.0)
            rotate(90f, 1f, 0f, 0f)
            rotate(-facing.horizontalAngle, 0f, 0f, 1f)
            translate(0.0, -1 * PIXEL, 0.0)
            val s = 0.5
            scale(s, s, s)
        } else {
            rotate(facing.horizontalAngle, 0f, 1f, 0f)
            translate(0.0, -1.9 * PIXEL, 0.0)
            val s = 0.9
            scale(s, s, s)
        }

        Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
    }

    fun renderTubeItem(stack: ItemStack) {
        val renderItem = Minecraft.getMinecraft().renderItem

        if (!renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
//            translate(0.0, -0.9 * PIXEL, 0.0)
//            rotate(90f, 1f, 0f, 0f)
//            translate(0.0, -1 * PIXEL, 0.0)
            val s = 12.px
            translate(0.0, (-0.5).px, 0.0)
            scale(s, s, s)
        } else {
            translate(0.0, (-2).px, 0.0)
        }

        renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
    }

    fun renderItemWithTransparency(stack: ItemStack, transform: ItemCameraTransforms.TransformType, alpha: Float) {
        var bakedModel = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(stack, null, null)
        if (stack.isNotEmpty) {
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

            bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedModel, transform, false)

            Minecraft.getMinecraft().renderItem.renderItem(stack, bakedModel)
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

    fun renderBox(box: AxisAlignedBB, color: IVector3 = vec3Of(1, 1, 1)) {
        val tes = Tessellator.getInstance()
        val t = tes.buffer
        val r = color.xf
        val g = color.yf
        val b = color.zf
        val a = 1f

//        glDisable(GL_TEXTURE_2D)
        bindTexture(0)
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
//        glEnable(GL_TEXTURE_2D)
    }

    fun renderLine(pos: IVector3, end: IVector3, color: IVector3 = vec3Of(1, 1, 1)) {
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

    fun facingRotate(facing: EnumFacing) {
        translate(0.5, 0.5, 0.5)
        when (facing) {
            EnumFacing.NORTH -> {
                rotate(90f, 0f, 1f, 0f)
                rotate(90f, 0f, 0f, 1f)
            }
            EnumFacing.SOUTH -> {
                rotate(90f, 0f, 1f, 0f)
                rotate(-90f, 0f, 0f, 1f)
            }
            EnumFacing.WEST -> rotate(-90f, 0f, 0f, 1f)
            EnumFacing.EAST -> rotate(90f, 0f, 0f, 1f)
            EnumFacing.UP -> rotate(180f, 1f, 0f, 0f)
            EnumFacing.DOWN -> rotate(0f, 1f, 0f, 0f)
        }

        translate(-0.5, -0.5, -0.5)
    }

    // this doesn't work
    fun renderMultiblockBoundingBoxes(te: IMultiblockModule) {
        val mb = te.multiblock ?: return
        val facing = te.multiblockFacing ?: return

        val global = mb.getGlobalCollisionBoxes().map {
            facing.rotateBox(vec3Of(0.5, 0.5, 0.5), it)
        }
        global.forEach { renderBox(it) }
    }

    // this works
    fun renderMultiblockCollisionBoxes(world: World, blockPos: BlockPos, te: IMultiblockModule) {

        val global = BlockMultiblock.getBoxes(world, blockPos, te)

        global.forEach { renderBox(it) }
    }


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
        val lines = str.split(BREAK_LINES_REGEX)

        // Print background and text
        pushMatrix()
        for (line in lines) {
            val j = fontrenderer.getStringWidth(line) / 2
            disableTexture2D()
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
            worldrenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldrenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldrenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldrenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            tessellator.draw()
            enableTexture2D()
            fontrenderer.drawString(line, -fontrenderer.getStringWidth(line) / 2, i, 553648127)
            translate(0f, (1 / f1) * 4f / 16f, 0f)
        }
        popMatrix()

        // Mark the text so it looks brighter
        enableDepth()
        depthMask(true)
        for (line in lines) {
            fontrenderer.drawString(line, -fontrenderer.getStringWidth(line) / 2, i, -1)
            translate(0f, (1 / f1) * 4f / 16f, 0f)
        }

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
            drawWireBetween(start, end, weight)
        }
    }

    fun drawWireBetween(start: Vec3d, end: Vec3d, weight: Double) {
        val tes = Tessellator.getInstance()
        val buffer = tes.buffer

        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)

        val points = interpolateWire(start, end, weight)

        for (p in 0..points.size - 2) {
            drawLine(buffer, points[p], points[p + 1])
        }
        tes.draw()
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

    fun multiblockPreview(ctx: MultiblockContext) {
        pushMatrix()
        rotateFromCenter(ctx.facing.opposite, 0f)
        renderMultiblockBlueprint(ctx)
        popMatrix()
    }

    fun drawCenter(size: Double = 0.0125) {
        renderBox(vec3Of(-size) createAABBUsing vec3Of(size))
    }

    fun setColor(color: Int) {
        GL11.glColor4f(
                ((color ushr 16) and 0xFF) / 255f,
                ((color ushr 8) and 0xFF) / 255f,
                (color and 0xFF) / 255f,
                ((color ushr 24) and 0xFF) / 255f
        )
    }

    fun getColorComponent(color: Int, component: Int): Float {
        return (color.split(component).toInt() and 0xFF) / 255f
    }


    // Original implementation: https://github.com/neilbartlett/color-temperature/blob/master/index.js
    fun tempToRGB(kelvin: Float): Triple<Float, Float, Float> {

        val temperature = kelvin / 100.0
        var red: Double
        var green: Double
        var blue: Double

        if (temperature < 66.0) {
            red = 255.0
        } else {
            // a + b x + c Log[x] /.
            // {a -> 351.97690566805693`,
            // b -> 0.114206453784165`,
            // c -> -40.25366309332127
            //x -> (kelvin/100) - 55}
            red = temperature - 55.0
            red = 351.97690566805693 + 0.114206453784165 * red - 40.25366309332127 * Math.log(red)
            if (red < 0) red = 0.0
            if (red > 255) red = 255.0
        }

        /* Calculate green */

        if (temperature < 66.0) {

            // a + b x + c Log[x] /.
            // {a -> -155.25485562709179`,
            // b -> -0.44596950469579133`,
            // c -> 104.49216199393888`,
            // x -> (kelvin/100) - 2}
            green = temperature - 2
            green = -155.25485562709179 - 0.44596950469579133 * green + 104.49216199393888 * Math.log(green)
            if (green < 0) green = 0.0
            if (green > 255) green = 255.0

        } else {

            // a + b x + c Log[x] /.
            // {a -> 325.4494125711974`,
            // b -> 0.07943456536662342`,
            // c -> -28.0852963507957`,
            // x -> (kelvin/100) - 50}
            green = temperature - 50.0
            green = 325.4494125711974 + 0.07943456536662342 * green - 28.0852963507957 * Math.log(green)
            if (green < 0) green = 0.0
            if (green > 255) green = 255.0

        }

        /* Calculate blue */

        if (temperature >= 66.0) {
            blue = 255.0
        } else {

            if (temperature <= 20.0) {
                blue = 0.0
            } else {

                // a + b x + c Log[x] /.
                // {a -> -254.76935184120902`,
                // b -> 0.8274096064007395`,
                // c -> 115.67994401066147`,
                // x -> kelvin/100 - 10}
                blue = temperature - 10
                blue = -254.76935184120902 + 0.8274096064007395 * blue + 115.67994401066147 * Math.log(blue)
                if (blue < 0) blue = 0.0
                if (blue > 255) blue = 255.0
            }
        }

        return Triple(Math.round(red).toFloat(), Math.round(green).toFloat(), Math.round(blue).toFloat())
    }

    fun withHeatColor(te: TileEntity, temp: Double, func: () -> Unit) {
        val (r, g, b) = tempToRGB(temp.toFloat())
        val heatVisibility = ((temp - 150.fromCelsiusToKelvin()) / 300).coerceIn(0.0, 1.0)

        val i = te.world.getCombinedLight(te.pos, 0)
        val j = i % 65536
        val k = i / 65536

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                max((15 shl 4) * heatVisibility, j.toDouble()).toFloat(),
                max((15 shl 4) * heatVisibility, k.toDouble()).toFloat()
        )

        // Set additive color
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD)
        GlStateManager.color(
                interpolate(0.0, r / 255.0, heatVisibility).toFloat(),
                interpolate(0.0, g / 255.0, heatVisibility).toFloat(),
                interpolate(0.0, b / 255.0, heatVisibility).toFloat(),
                1f
        )
        func()

        // Reset color
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE)
        GlStateManager.color(1f, 1f, 1f, 1f)
        setDefaultLight(te)
    }

    fun setDefaultLight(te: TileEntity) {
        RenderHelper.enableStandardItemLighting()
        val i = te.world.getCombinedLight(te.pos, 0)
        val j = i % 65536
        val k = i / 65536
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat(), k.toFloat())
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun colorFromRGB(r: Float, g: Float, b: Float): Int {
        val red = (255 * r).toInt()
        val green = (255 * g).toInt()
        val blue = (255 * b).toInt()

        return (0xFF shl 24) or ((red and 0xFF) shl 16) or ((green and 0xFF) shl 8) or (blue and 0xFF)
    }

    fun renderIO(ioModule: ModuleMultiblockIO) {
        pushMatrix()
        rotateFromCenter(ioModule.facing(), 0f)

        val cache = ioModule.clientCache ?: mutableListOf<MutableCubeCache>().also { ioModule.clientCache = it }

        while (cache.size < ioModule.connectionSpots.size) {
            cache.add(MutableCubeCache())
        }

        ioModule.connectionSpots.forEachIndexed { index, spot ->
            val box = cache[index]
            val sprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite("minecraft:blocks/water_still")
            val finalPos = spot.pos

            box.sprites = listOf(sprite)
            box.pos = finalPos.toVec3d()
            box.size = Vector3.ONE
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            setColor(spot.capability.hashCode() or 0xFF000000.toInt())
            box.render()
            GL11.glColor4f(1f, 1f, 1f, 1f)
            GlStateManager.color(1f, 1f, 1f)
        }
        popMatrix()
    }
}