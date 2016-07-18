package com.cout970.magneticraft.client.render.tileentity

import com.cout970.loader.api.ModelRegistry
import com.cout970.loader.api.model.IDynamicModel
import com.cout970.magneticraft.tileentity.TileBase
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import java.util.*

/**
 * Created by cout970 on 16/07/2016.
 */
abstract class TileEntityRenderer<T: TileBase> : TileEntitySpecialRenderer<T>() {

    abstract override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int)

    fun getModel(res: ResourceLocation): IDynamicModel{
        return ModelRegistry.getDynamicModel(res) ?:
                throw MissingResourceException("Model not found", res.toString(), "")
    }

    open fun onModelRegistryReload(){}
}