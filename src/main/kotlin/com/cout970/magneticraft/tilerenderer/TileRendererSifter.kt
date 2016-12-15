package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelFilter
import com.cout970.loader.api.model.IModelPart
import com.cout970.loader.api.model.IObjGroup
import com.cout970.magneticraft.multiblock.impl.MultiblockHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileSifter
import com.cout970.magneticraft.util.resource
import com.google.common.base.Predicates
import net.minecraft.client.renderer.GlStateManager

/**
 * Created by cout970 on 21/08/2016.
 */
object TileRendererSifter : TileEntityRenderer<TileSifter>() {

    val texture = resource("textures/models/sifter.png")
    lateinit var noHammer: ICachedModel
    lateinit var hammer: ICachedModel

    override fun renderTileEntityAt(te: TileSifter, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
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

        rotateFromCenter(te.direction, 0f)
        bindTexture(texture)
        noHammer.render()

        val speed = 360f

        hammer.render()
        GlStateManager.popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val model = getModelObj(resource("models/block/obj/sifter.obj"))
            val hasFan = object : IModelFilter {
                override fun apply(it: IModelPart?): Boolean = if (it is IObjGroup) it.getName().contains("head") || it.getName().contains("rod") else false
            }
            noHammer = ModelCacheFactory.createCachedModel(model.filter(Predicates.not(hasFan)), 1)
            hammer = ModelCacheFactory.createCachedModel(model.filter(hasFan), 1)
        } catch (e: Exception) {
        }
    }
}