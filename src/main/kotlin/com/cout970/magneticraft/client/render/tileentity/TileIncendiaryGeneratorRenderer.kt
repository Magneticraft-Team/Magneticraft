package com.cout970.magneticraft.client.render.tileentity

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.ModelRegistry
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelCube
import com.cout970.loader.api.model.IModelFilter
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.resource
import com.google.common.base.Predicates
import net.minecraft.client.renderer.GlStateManager.*

/**
 * Created by cout970 on 16/07/2016.
 */
object TileIncendiaryGeneratorRenderer : TileEntityRenderer<TileIncendiaryGenerator>() {

    val texture = resource("textures/models/incendiary_generator.png")
    var block: ICachedModel? = null
    var fan: ICachedModel? = null

    override fun renderTileEntityAt(te: TileIncendiaryGenerator, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        pushMatrix()
        translate(x, y - 1, z)
        val dir = PROPERTY_DIRECTION[te.blockType.getStateFromMeta(te.blockMetadata)]
        rotateFromCenter(dir, -90f)
        bindTexture(texture)
        block?.render()
        translate(0.5f, 0f, 0.5f)
        if (te.fanAnimation.active) {
            te.fanAnimation.updateAnimation()
        }
        rotate(te.fanAnimation.getRotationState(te.heat * 0.5f), 0f, 1f, 0f)
        translate(-0.5f, -0f, -0.5f)
        fan?.render()
        popMatrix()
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        val model = ModelRegistry.getDynamicModel(resource("models/block/incendiary_generator.mcm"))
        val hasFan = IModelFilter {
            if (it is IModelCube) it.name.contains("Fan") else false
        }
        val blockModel = model.filter(Predicates.not(hasFan))
        val fanModel = model.filter(hasFan)
        block = ModelCacheFactory.createCachedModel(blockModel, 128)
        fan = ModelCacheFactory.createCachedModel(fanModel, 128)
    }
}