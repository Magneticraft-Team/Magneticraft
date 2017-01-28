package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.magneticraft.multiblock.impl.MultiblockSifter
import com.cout970.magneticraft.tileentity.multiblock.TileSifter
import com.cout970.magneticraft.util.resource

/**
 * Created by cout970 on 21/08/2016.
 */
object TileRendererSifter : TileEntityRenderer<TileSifter>() {

    val texture = resource("textures/models/sifter.png")
    var allModel: ICachedModel? = null

    override fun renderTileEntityAt(te: TileSifter, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        if (!te.active) {
            pushMatrix()
            translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockSifter)
            popMatrix()
            return
        }
        pushMatrix()
        translate(x, y, z)
        rotateFromCenter(te.direction, 1800f)
        translate(z = 2.0)
        bindTexture(texture)
        allModel?.render()
        popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val model = getModelObj(resource("models/block/obj/sifter.obj"))
            this.allModel = ModelCacheFactory.createCachedModel(model.model, 1)
        } catch (e: Exception) {
        }
    }
}