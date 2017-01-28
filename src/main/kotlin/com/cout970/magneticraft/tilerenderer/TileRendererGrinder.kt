package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.magneticraft.multiblock.impl.MultiblockGrinder
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.util.resource

/**
 * Created by cout970 on 21/08/2016.
 */
object TileRendererGrinder : TileEntityRenderer<TileGrinder>() {

    val texture = resource("textures/models/grinder.png")
    var model: ICachedModel? = null

    override fun renderTileEntityAt(te: TileGrinder, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        if (!te.active) {
            pushMatrix()
            translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockGrinder)
            popMatrix()
            return
        }
        pushMatrix()
        translate(x, y, z)
        rotateFromCenter(te.direction, 180f)
        translate(z = -1.0)
        bindTexture(texture)
        model?.render()
        popMatrix()
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