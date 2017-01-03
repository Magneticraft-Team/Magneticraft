package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.magneticraft.multiblock.impl.MultiblockGrinder
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.GlStateManager

/**
 * Created by cout970 on 21/08/2016.
 */
object TileRendererGrinder : TileEntityRenderer<TileGrinder>() {

    val texture = resource("textures/models/grinder.png")
    lateinit var model: ICachedModel

    override fun renderTileEntityAt(te: TileGrinder, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!te.active) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockGrinder)
            GlStateManager.popMatrix()
            return
        }
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        rotateFromCenter(te.direction, 0f)
        GlStateManager.translate(0.0, 0.0, 2.0)
        bindTexture(texture)
//        model.render()
        GlStateManager.popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val dyn = getModelObj(resource("models/block/obj/grinder.obj"))
            this.model = ModelCacheFactory.createCachedModel(dyn.model, 1)
        } catch (e: Exception) {
        }
    }
}