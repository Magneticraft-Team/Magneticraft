package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.magneticraft.multiblock.impl.MultiblockSolarPanel
import com.cout970.magneticraft.tileentity.multiblock.TileSolarPanel
import com.cout970.magneticraft.util.resource

/**
 * Created by cout970 on 2016/09/06.
 */
object TileRendererSolarPanel : TileEntityRenderer<TileSolarPanel>() {

    val texture = resource("textures/models/solar_panel.png")
    lateinit var model: ICachedModel

    override fun renderTileEntityAt(te: TileSolarPanel, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!te.active) {
            pushMatrix()
            translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockSolarPanel)
            popMatrix()
            return
        }
        pushMatrix()
        translate(x, y, z)
        rotateFromCenter(te.direction, 90f)
        translate(-1.0, 0.0, 0.0)
        bindTexture(texture)

        model.render()
        popMatrix()
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