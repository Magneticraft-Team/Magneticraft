package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.magneticraft.multiblock.impl.MultiblockSolarPanel
import com.cout970.magneticraft.tileentity.multiblock.TileSolarPanel
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.GlStateManager

/**
 * Created by cout970 on 2016/09/06.
 */
object TileSolarPanelRenderer : TileEntityRenderer<TileSolarPanel>() {

    val texture = resource("textures/models/solar_panel.png")
    lateinit var model: ICachedModel

    override fun renderTileEntityAt(te: TileSolarPanel, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!te.active) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockSolarPanel)
            GlStateManager.popMatrix()
            return
        }
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        rotateFromCenter(te.direction, 90f)
        GlStateManager.translate(-1.0, 0.0, 0.0)
        bindTexture(texture)

        model.render()
        GlStateManager.popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val dyn = getModelObj(resource("models/block/obj/solar_panel.obj"))
            this.model = ModelCacheFactory.createCachedModel(dyn.model, 1)
        } catch (e: Exception) {
        }
    }
}