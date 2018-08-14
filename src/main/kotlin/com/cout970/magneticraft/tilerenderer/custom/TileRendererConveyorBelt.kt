package com.cout970.magneticraft.tilerenderer.custom

import com.cout970.magneticraft.block.AutomaticMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import com.cout970.modelloader.api.Model
import com.cout970.modelloader.api.ModelEntry
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilities
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 2017/06/16.
 */
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