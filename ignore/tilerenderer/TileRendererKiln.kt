package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelPart
import com.cout970.loader.api.model.IObjGroup
import com.cout970.magneticraft.multiblock.impl.MultiblockKiln
import com.cout970.magneticraft.tileentity.multiblock.TileKiln
import com.cout970.magneticraft.util.resource
import com.google.common.base.Predicate

/**
 * Created by cout970 on 21/08/2016.
 */
object TileRendererKiln : TileEntityRenderer<TileKiln>() {

    val texture = resource("textures/models/kiln.png")
    lateinit var model: ICachedModel
    lateinit var door: ICachedModel

    override fun renderTileEntityAt(te: TileKiln, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        if (!te.active) {
            pushMatrix()
            translate(x, y, z)
            rotateFromCenter(te.direction, 0f)
            renderMultiblockBlueprint(MultiblockKiln)
            popMatrix()
            return
        }
        pushMatrix()
        translate(x, y, z)
        rotateFromCenter(te.direction, 180f)
        translate(z = -2.0)
        bindTexture(texture)
        model.render()
        if (te.doorOpen) {
            rotate(90, y = 1)
        }
        door.render()
        popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val dyn = getModelObj(resource("models/block/obj/beehive_kiln.obj"))
            val predicate: Predicate<IModelPart> = Predicate {
                (it as? IObjGroup)?.getName()?.contains("door") ?: false
            }

            this.model = ModelCacheFactory.createCachedModel(dyn.filterNot(predicate), 1)
            this.door = ModelCacheFactory.createCachedModel(dyn.filter(predicate), 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}