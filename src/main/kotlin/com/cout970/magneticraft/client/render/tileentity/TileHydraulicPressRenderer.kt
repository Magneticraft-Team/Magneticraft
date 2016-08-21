package com.cout970.magneticraft.client.render.tileentity

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelFilter
import com.cout970.loader.api.model.IModelPart
import com.cout970.loader.api.model.IObjGroup
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.util.resource
import com.google.common.base.Predicates
import net.minecraft.client.renderer.GlStateManager

/**
 * Created by cout970 on 21/08/2016.
 */
object TileHydraulicPressRenderer : TileEntityRenderer<TileHydraulicPress>() {

    val texture = resource("textures/models/hydraulic_press.png")
    lateinit var noHammer: ICachedModel
    lateinit var hammer: ICachedModel

    override fun renderTileEntityAt(te: TileHydraulicPress, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!te.active) return
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        rotateFromCenter(te.direction, 0f)
        bindTexture(texture)
        noHammer.render()
        te.hammerAnimation.updateAnimation()
        val state = (te.hammerAnimation.getMotionState(50f) + 1f) * 0.5f
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