package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.AutomaticMachines
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileInserter
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import com.cout970.modelloader.api.Model
import com.cout970.modelloader.api.ModelEntry
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilities
import com.cout970.modelloader.api.animation.AnimatedModel
import com.cout970.modelloader.api.util.TRSTransformation
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemSkull
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.ForgeHooksClient

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileFeedingTrough::class)
object TileRendererFeedingTrough : BaseTileRenderer<TileFeedingTrough>() {

    override fun init() {
        createModel(AutomaticMachines.feedingTrough)
    }

    override fun render(te: TileFeedingTrough) {

        val item = te.invModule.inventory[0]
        val level = when {
            item.count > 32 -> 4
            item.count > 16 -> 3
            item.count > 8 -> 2
            item.count > 0 -> 1
            else -> 0
        }

        Utilities.rotateFromCenter(te.facing, 90f)
        renderModel("default")
        if (level > 0) {
            Utilities.rotateFromCenter(EnumFacing.NORTH, 90f)
            stackMatrix {
                renderSide(level, item)
                rotate(180f, 0.0f, 1.0f, 0.0f)
                translate(-1.0, 0.0, -2.0)
                renderSide(level, item)
            }
        }
    }

    private fun renderSide(level: Int, item: ItemStack) {

        if (level >= 1) {
            pushMatrix()
            transform(Vec3d(2.0, 1.1, 6.0),
                Vec3d(90.0, 0.0, 0.0),
                Vec3d(0.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()


            pushMatrix()
            transform(Vec3d(7.5, 1.5, 7.0),
                Vec3d(90.0, 30.0, 0.0),
                Vec3d(0.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }

        if (level >= 2) {
            pushMatrix()
            transform(Vec3d(0.0, 2.0, 12.0),
                Vec3d(90.0, -30.0, 0.0),
                Vec3d(16.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(16.5, 2.5, 12.5),
                Vec3d(90.0, -30.0, 0.0),
                Vec3d(16.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }

        if (level >= 3) {
            pushMatrix()
            transform(Vec3d(1.0, 1.0, 7.0),
                Vec3d(-10.0, 22.0, 4.0),
                Vec3d(0.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(0.0, 1.0, 15.0),
                Vec3d(10.0, -22.0, -4.0),
                Vec3d(16.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(-2.5, -2.0, -4.0),
                Vec3d(20.0, -90.0, 5.0),
                Vec3d(0.0, 0.0, 8.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }

        if (level >= 4) {
            pushMatrix()
            transform(Vec3d(5.0, 0.0, 4.5),
                Vec3d(-10.0, 0.0, 0.0),
                Vec3d(0.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(7.0, 0.0, 11.0),
                Vec3d(14.0, 0.0, 0.0),
                Vec3d(0.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(12.0, 0.0, 10.0),
                Vec3d(14.0, 0.0, 0.0),
                Vec3d(0.0, 0.0, 0.0),
                Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }
    }

    private fun transform(pos: Vec3d, rot: Vec3d, rotPos: Vec3d, scale: Vec3d) {
        translate(PIXEL * 16, 0.0, 0.0)
        rotate(0, -90, 0)

        translate(pos.xd * PIXEL, pos.yd * PIXEL, pos.zd * PIXEL)

        translate(rotPos.xd * PIXEL, rotPos.yd * PIXEL, rotPos.zd * PIXEL)

        rotate(rot.xd, rot.yd, rot.zd)
        translate(-rotPos.xd * PIXEL, -rotPos.yd * PIXEL, -rotPos.zd * PIXEL)

        translate(PIXEL * 4, 0.0, 0.0)
        GlStateManager.scale(scale.xd, scale.yd, scale.zd)
    }

    private fun rotate(x: Number, y: Number, z: Number) {
        rotate(z, 0f, 0f, 1f)
        rotate(y, 0f, 1f, 0f)
        rotate(x, 1f, 0f, 0f)
    }

    private fun renderItem(stack: ItemStack) {
        Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
    }
}

@RegisterRenderer(TileInserter::class)
object TileRendererInserter : BaseTileRenderer<TileInserter>() {

    override fun init() {
        val item = FilterNotString("item")
        createModel(AutomaticMachines.inserter,
            ModelSelector("animation0", item, FilterRegex("animation0", FilterTarget.ANIMATION)),
            ModelSelector("animation1", item, FilterRegex("animation1", FilterTarget.ANIMATION)),
            ModelSelector("animation2", item, FilterRegex("animation2", FilterTarget.ANIMATION)),
            ModelSelector("animation3", item, FilterRegex("animation3", FilterTarget.ANIMATION)),
            ModelSelector("animation4", item, FilterRegex("animation4", FilterTarget.ANIMATION)),
            ModelSelector("animation5", item, FilterRegex("animation5", FilterTarget.ANIMATION)),
            ModelSelector("animation6", item, FilterRegex("animation6", FilterTarget.ANIMATION)),
            ModelSelector("animation7", item, FilterRegex("animation7", FilterTarget.ANIMATION)),
            ModelSelector("animation8", item, FilterRegex("animation8", FilterTarget.ANIMATION)),
            ModelSelector("animation9", item, FilterRegex("animation9", FilterTarget.ANIMATION))
        )
    }

    override fun render(te: TileInserter) {
        Utilities.rotateFromCenter(te.facing, 180f)
        val mod = te.inserterModule
        val transition = mod.transition

        val extra = if (mod.moving) ticks else 0f
        time = ((mod.animationTime + extra) / mod.maxAnimationTime).coerceAtMost(1f).toDouble() * 20 * 0.33
        renderModel(transition.animation)

        val item = te.inventory[0]
        if (item.isEmpty) return

        // animation0 is used to get the articulated node because Transition.ROTATING discards the
        // info about translation/rotation of the inner nodes
        val cache0 = getModel("animation0") as? AnimationRenderCache ?: return
        val cache1 = getModel(transition.animation) as? AnimationRenderCache ?: return
        val node = cache0.model.rootNodes.firstOrNull() ?: return
        val anim = cache1.model

        val localTime = ((time / 20.0) % cache1.model.length.toDouble()).toFloat()
        val trs = getGlobalTransform(item, anim, node, localTime)

        pushMatrix()
        ForgeHooksClient.multiplyCurrentGlMatrix(trs.matrix.apply { transpose() })
        translate(0.0, (-7.5).px, 0.0)

        if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(item) || item.item is ItemSkull) {
            // 2D item
            scale(0.75)
        } else {
            // 3D block
            rotate(180f, 0f, 1f, 0f)
            translate(0.0, (-1.9).px, 0.0)
            scale(0.9)
        }

        Minecraft.getMinecraft().renderItem.renderItem(item, ItemCameraTransforms.TransformType.GROUND)
        popMatrix()
    }

    fun getGlobalTransform(item: ItemStack, anim: AnimatedModel, node: AnimatedModel.Node, localTime: Float): TRSTransformation {
        val trs = anim.getTransform(node, localTime)
        if (node.children.isEmpty()) return trs
        return trs * getGlobalTransform(item, anim, node.children[0], localTime)
    }
}

@RegisterRenderer(TileConveyorBelt::class)
object TileRendererConveyorBelt : BaseTileRenderer<TileConveyorBelt>() {

    var belt: ModelCache? = null
    var cornerBelt: ModelCache? = null

    override fun init() {
        createModel(AutomaticMachines.conveyorBelt, listOf(
            ModelSelector("back_legs", FilterRegex("back_leg.*")),
            ModelSelector("front_legs", FilterRegex("front_leg.*")),
            ModelSelector("lateral_left", FilterRegex("lateral_left")),
            ModelSelector("lateral_right", FilterRegex("lateral_right")),
            ModelSelector("panel_left", FilterRegex("panel_left")),
            ModelSelector("panel_right", FilterRegex("panel_right"))
        ), "base")

        createModel(AutomaticMachines.conveyorBelt,
            listOf(ModelSelector("corner", FilterAlways)), "corner_base")

        val anim = modelOf(AutomaticMachines.conveyorBelt, "anim")()
        val cornerAnim = modelOf(AutomaticMachines.conveyorBelt, "corner_anim")()

        //cleaning
        cornerBelt?.close()
        belt?.close()

        val beltModel = ModelLoaderApi.getModelEntry(anim) ?: return
        val cornerBeltModel = ModelLoaderApi.getModelEntry(cornerAnim) ?: return

        belt = updateTexture(beltModel, resource("blocks/machines/conveyor_belt_anim"))
        cornerBelt = updateTexture(cornerBeltModel, resource("blocks/machines/conveyor_belt_anim"))
    }


    override fun render(te: TileConveyorBelt) {
        Utilities.rotateFromCenter(te.facing)
        renderStaticParts(te)
        renderDynamicParts(te, ticks)
        translate(0f, 12.5 * PIXEL, 0f)

        //debug hitboxes
//        renderHitboxes(te)
    }

    // debug
    fun renderHitboxes(te: TileConveyorBelt) {
        te.conveyorModule.boxes.forEach {
            Utilities.renderBox(it.getHitBox())
        }
    }

    // debug bitmaps
    fun renderBitmap(te: TileConveyorBelt, x: Double, y: Double, z: Double) {
        stackMatrix {
            translate(x, y + 1f, z)
            Utilities.rotateFromCenter(te.facing)

            Utilities.renderBox(vec3Of(1, 0, 0) * PIXEL toAABBWith vec3Of(2, 1, 1) * PIXEL,
                vec3Of(0, 1, 0))
            Utilities.renderBox(vec3Of(0, 0, 1) * PIXEL toAABBWith vec3Of(1, 1, 2) * PIXEL,
                vec3Of(0, 0, 1))

            val bitmap2 = te.conveyorModule.generateGlobalBitMap()

            for (i in 0 until 16) {
                for (j in 0 until 16) {

                    val color = if (!bitmap2[i, j]) vec3Of(1, 1, 1) else vec3Of(0, 0, 1)
                    val h = if (bitmap2[i, j]) 1 else 0

                    Utilities.renderBox(
                        vec3Of(i, h, j) * PIXEL toAABBWith vec3Of(i + 1, h, j + 1) * PIXEL, color)
                }
            }
        }
    }


    fun renderDynamicParts(te: TileConveyorBelt, partialTicks: Float) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        te.conveyorModule.boxes.forEach { box ->
            stackMatrix {
                val pos = box.getPos(partialTicks)
                translate(pos.xd, 13.5 * PIXEL, pos.zd)
                Utilities.renderItem(box.item, te.facing)
            }
        }
    }


    fun renderStaticParts(te: TileConveyorBelt) {
        val mod = te.conveyorModule

        if (mod.isCorner) {
            stackMatrix {

                if (mod.hasRight()) {
                    rotate(90f, 0f, 1f, 0f)
                    scale(-1f, 1f, 1f)
                    GlStateManager.cullFace(GlStateManager.CullFace.FRONT)

                    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    cornerBelt?.render()

                    renderModel("corner")

                    GlStateManager.cullFace(GlStateManager.CullFace.BACK)
                } else {
                    translate(1f, 0f, 0f)
                    rotate(-90f, 0f, 1f, 0f)

                    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    cornerBelt?.render()

                    renderModel("corner")
                }
            }

        } else {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            belt?.render()

            renderModel("default")

            if (mod.hasBack()) {
                stackMatrix {
                    translate(0f, 0f, PIXEL)
                    renderModel("back_legs")
                }
            } else {
                renderModel("back_legs")
            }

            if (!mod.hasFront()) {
                renderModel("front_legs")
            }

            if (mod.hasLeft()) {
                renderModel("lateral_left")
            } else {
                renderModel("panel_left")
            }

            if (mod.hasRight()) {
                renderModel("lateral_right")
            } else {
                renderModel("panel_right")
            }
        }
    }

    private fun updateTexture(model: ModelEntry, texture: ResourceLocation): ModelCache {
        val raw = model.raw as Model.Mcx
        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val animTexture = textureMap.getAtlasSprite(texture.toString())
        val finalModel = ModelTransform.updateModelUvs(raw.data, animTexture)
        return ModelCache { ModelUtilities.renderModel(finalModel) }
    }
}
