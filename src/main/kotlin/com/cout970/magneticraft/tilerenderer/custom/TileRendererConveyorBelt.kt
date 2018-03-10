package com.cout970.magneticraft.tilerenderer.custom

import com.cout970.magneticraft.block.AutomaticMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import com.cout970.modelloader.QuadProvider
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilties
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 2017/06/16.
 */
@RegisterRenderer(TileConveyorBelt::class)
object TileRendererConveyorBelt : TileRenderer<TileConveyorBelt>() {

    val texture = resource("textures/blocks/machines/conveyor_belt.png")
    val cornerTexture = resource("textures/blocks/machines/conveyor_belt_corner.png")

    var rest: ModelCache? = null
    var belt: ModelCache? = null
    var backLegs: ModelCache? = null
    var frontLegs: ModelCache? = null
    var lateralLeft: ModelCache? = null
    var lateralRight: ModelCache? = null
    var panelLeft: ModelCache? = null
    var panelRight: ModelCache? = null
    var corner: ModelCache? = null
    var cornerBelt: ModelCache? = null

    override fun renderTileEntityAt(te: TileConveyorBelt, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {

        // error loading the model
        rest ?: return

        stackMatrix {
            translate(x, y, z)
            Utilities.rotateFromCenter(te.facing)
            renderStaticParts(te)
            renderDynamicParts(te, partialTicks)
            translate(0f, 12.5 * PIXEL, 0f)
            //debug hitboxes
//            renderHitboxes(te)
        }

//        if (te.pos.xi == -391 && te.pos.zi == -280 ||
//            te.pos.xi == -392 && te.pos.zi == -280 ||
//            te.pos.xi == -392 && te.pos.zi == -281 ||
//            te.pos.xi == -391 && te.pos.zi == -281 ||
//            te.pos.xi == -391 && te.pos.zi == -280) {
//            renderBitmap(te, x, y, z)
//        }
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

                    bindTexture(cornerTexture)
                    corner?.render()

                    GlStateManager.cullFace(GlStateManager.CullFace.BACK)
                } else {
                    translate(1f, 0f, 0f)
                    rotate(-90f, 0f, 1f, 0f)

                    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    cornerBelt?.render()

                    bindTexture(cornerTexture)
                    corner?.render()
                }
            }

        } else {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            belt?.render()

            bindTexture(texture)
            rest?.render()

            if (mod.hasBack()) {
                stackMatrix {
                    translate(0f, 0f, PIXEL)
                    backLegs?.render()
                }
            } else {
                backLegs?.render()
            }

            if (!mod.hasFront()) {
                frontLegs?.render()
            }

            if (mod.hasLeft()) {
                lateralLeft?.render()
            } else {
                panelLeft?.render()
            }

            if (mod.hasRight()) {
                lateralRight?.render()
            } else {
                panelRight?.render()
            }
        }
    }

    override fun onModelRegistryReload() {
        val base = modelOf(AutomaticMachines.conveyorBelt, "base")()
        val anim = modelOf(AutomaticMachines.conveyorBelt, "anim")()
        val cornerBase = modelOf(AutomaticMachines.conveyorBelt, "corner_base")()
        val cornerAnim = modelOf(AutomaticMachines.conveyorBelt, "corner_anim")()
        //cleaning

        corner?.close()
        cornerBelt?.close()
        rest?.close()
        belt?.close()
        backLegs?.close()
        frontLegs?.close()
        lateralLeft?.close()
        lateralRight?.close()

        rest = ModelCacheFactory.createCache(base) {
            !it.startsWith("lateral_left") && !it.startsWith("lateral_right") && !it.startsWith("panel_left") &&
            !it.startsWith("panel_right") && !it.startsWith("back_leg") && !it.startsWith("front_leg")
        }
        lateralLeft = ModelCacheFactory.createCache(base) { it.startsWith("lateral_left") }
        lateralRight = ModelCacheFactory.createCache(base) { it.startsWith("lateral_right") }
        panelLeft = ModelCacheFactory.createCache(base) { it.startsWith("panel_left") }
        panelRight = ModelCacheFactory.createCache(base) { it.startsWith("panel_right") }
        backLegs = ModelCacheFactory.createCache(base) { it.startsWith("back_leg") }
        frontLegs = ModelCacheFactory.createCache(base) { it.startsWith("front_leg") }
        corner = ModelCacheFactory.createCache(cornerBase)

        val beltModel = ModelLoaderApi.getModel(anim) ?: return
        val cornerBeltModel = ModelLoaderApi.getModel(cornerAnim) ?: return

        belt = updateTexture(beltModel, resource("blocks/machines/conveyor_belt_anim"))
        cornerBelt = updateTexture(cornerBeltModel, resource("blocks/machines/conveyor_belt_anim"))
    }

    private fun updateTexture(model: QuadProvider, texture: ResourceLocation): ModelCache {
        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val animTexture = textureMap.getAtlasSprite(texture.toString())
        val finalModel = ModelTransform.updateModelUvs(model, animTexture)
        return ModelCache { ModelUtilties.renderModelParts(finalModel.modelData, finalModel.modelData.parts) }
    }
}