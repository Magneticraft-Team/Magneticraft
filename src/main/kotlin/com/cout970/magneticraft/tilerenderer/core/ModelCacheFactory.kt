package com.cout970.magneticraft.tilerenderer.core

import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilties.renderModelParts
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/16.
 */
object ModelCacheFactory {

    fun createCache(loc: ModelResourceLocation, filter: (String) -> Boolean): ModelCache? {
        val model = ModelLoaderApi.getModel(loc) ?: return null
        return ModelCache {
            renderModelParts(model.modelData, model.modelData.parts.filter { filter(it.name) })
        }
    }
}