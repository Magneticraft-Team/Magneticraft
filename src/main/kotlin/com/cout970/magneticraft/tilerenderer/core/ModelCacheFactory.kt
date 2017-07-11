package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.util.addPostfix
import com.cout970.magneticraft.util.addPrefix
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilties.renderModelParts
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/16.
 */
object ModelCacheFactory {

    fun createCache(loc: ModelResourceLocation, filter: (String) -> Boolean = { true }): ModelCache? {
        val model = ModelLoaderApi.getModel(loc) ?: return null
        val parts = model.modelData.parts.filter { filter(it.name) }
        return ModelCache {
            renderModelParts(model.modelData, parts)
        }.apply { texture = parts.firstOrNull()?.texture?.addPrefix("textures/")?.addPostfix(".png") }
    }

    fun createMultiTextureCache(loc: ModelResourceLocation, filter: (String) -> Boolean = { true }): List<ModelCache> {
        val model = ModelLoaderApi.getModel(loc) ?: return emptyList()
        val parts = model.modelData.parts.filter { filter(it.name) }

        val textureGrouped = parts.groupBy { it.texture }
        return textureGrouped.map {
            ModelCache {
                renderModelParts(model.modelData, it.value)
            }.apply { texture = it.key.addPrefix("textures/").addPostfix(".png") }
        }
    }
}