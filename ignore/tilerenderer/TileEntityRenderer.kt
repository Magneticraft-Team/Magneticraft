package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelRegistry
import com.cout970.loader.api.model.IDynamicModel
import com.cout970.loader.api.model.IModelPart
import com.cout970.magneticraft.tileentity.TileBase
import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import java.util.*

/**
 * Created by cout970 on 16/07/2016.
 */
abstract class TileEntityRenderer<T : TileBase> : TileEntitySpecialRenderer<T>() {

    abstract override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int)

    fun getModel(res: ResourceLocation): IDynamicModel {
        return ModelRegistry.getDynamicModel(res) ?:
                throw MissingResourceException("Model not found", res.toString(), "")
    }

    fun getModelObj(res: ResourceLocation): IDynamicModel {
        return ModelRegistry.getDynamicObjModel(res) ?:
                throw MissingResourceException("Model not found", res.toString(), "")
    }

    open fun onModelRegistryReload() {
    }

    fun pushMatrix() = GlStateManager.pushMatrix()
    fun popMatrix() = GlStateManager.popMatrix()

    fun translate(x: Number = 0, y: Number = 0, z: Number = 0) = GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
    fun rotate(angle: Number, x: Number = 0, y: Number = 0, z: Number = 0) = GlStateManager.rotate(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())

    fun IDynamicModel.filterNot(filter: Predicate<IModelPart>): IModelPart? = filter(Predicates.not(filter))
}