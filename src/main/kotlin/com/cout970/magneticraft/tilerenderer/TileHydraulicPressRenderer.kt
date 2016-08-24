package com.cout970.magneticraft.tilerenderer

import coffee.cypher.mcextlib.extensions.inventories.get
import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelFilter
import com.cout970.loader.api.model.IModelPart
import com.cout970.loader.api.model.IObjGroup
import com.cout970.magneticraft.multiblock.impl.MultiblockHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.util.resource
import com.google.common.base.Predicates
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemSkull

/**
 * Created by cout970 on 21/08/2016.
 */
object TileHydraulicPressRenderer : TileEntityRenderer<TileHydraulicPress>() {

    val texture = resource("textures/models/hydraulic_press.png")
    lateinit var noHammer: ICachedModel
    lateinit var hammer: ICachedModel

    override fun renderTileEntityAt(te: TileHydraulicPress, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!te.active) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockHydraulicPress)
            GlStateManager.popMatrix()
            return
        }
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        val stack = te.inventory[0]
        if (stack != null) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(0.5, 0.9375, 0.3125)
            if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
                GlStateManager.translate(0.0, -0.045, 0.125)
                GlStateManager.rotate(90f, 1f, 0f, 0f)
            } else {
                GlStateManager.translate(0.0, -0.125, 0.0625 * 3)
            }

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
            GlStateManager.popMatrix()
        }

        rotateFromCenter(te.direction, 0f)
        bindTexture(texture)
        noHammer.render()

        val speed = 480f
        if (te.craftingProcess.isWorking(te.world)) {
            te.hammerAnimation.updateAnimation()
        } else if ((te.hammerAnimation.getMotionState(speed) + 1f) * 0.5f > 0.5) {
            te.hammerAnimation.updateAnimation()
        } else {
            te.hammerAnimation.resetDelta()
        }

        val state = (te.hammerAnimation.getMotionState(speed) + 1f) * 0.5f
        GlStateManager.translate(0f, -state * 8f / 16f, 0f)
        hammer.render()
        GlStateManager.popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val model = getModelObj(resource("models/block/obj/hydraulic_press.obj"))
            val hasFan = object : IModelFilter {
                override fun apply(it: IModelPart?): Boolean = if (it is IObjGroup) it.getName().contains("head") else false
            }
            noHammer = ModelCacheFactory.createCachedModel(model.filter(Predicates.not(hasFan)), 1)
            hammer = ModelCacheFactory.createCachedModel(model.filter(hasFan), 1)
        } catch (e: Exception) {
        }
    }
}