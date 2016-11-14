package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.magneticraft.multiblock.impl.MultiblockKiln
import com.cout970.magneticraft.tileentity.multiblock.TileKiln
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.GlStateManager

/**
 * Created by cout970 on 21/08/2016.
 */
object TileRendererKiln : TileEntityRenderer<TileKiln>() {

    val texture = resource("textures/models/kiln.png")
    lateinit var model: ICachedModel

    override fun renderTileEntityAt(te: TileKiln, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!te.active) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockKiln)
            GlStateManager.popMatrix()
            return
        }
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        rotateFromCenter(te.direction, 0f)
        GlStateManager.translate(0.0, 0.0, 2.0)
        bindTexture(texture)
        if (te.doorOpen) {
            model.render()
        } else {
            model.render()
        }
        GlStateManager.popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val dyn = getModelObj(resource("models/block/obj/kiln.obj"))
            this.model = ModelCacheFactory.createCachedModel(dyn.model, 1)
        } catch (e: Exception) {
        }
    }
}