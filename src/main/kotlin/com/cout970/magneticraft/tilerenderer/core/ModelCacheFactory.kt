package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.util.addPostfix
import com.cout970.magneticraft.util.addPrefix
import com.cout970.modelloader.api.Model
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilities.renderModelParts
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/16.
 */
object ModelCacheFactory {

    fun createCache(loc: ModelResourceLocation, filter: (String) -> Boolean = { true }): ModelCache? {
        val model = ModelLoaderApi.getModelEntry(loc) ?: return null
        val raw = model.raw as? Model.Mcx ?: return null
        val parts = raw.data.parts.filter { filter(it.name) }

        return ModelCache { renderModelParts(raw.data, parts) }.apply {
            val material = parts.firstOrNull()?.texture
            texture = material?.addPrefix("textures/")?.addPostfix(".png")
        }
    }

    fun createMultiTextureCache(loc: ModelResourceLocation, filter: (String) -> Boolean = { true }): List<ModelCache> {
        val model = ModelLoaderApi.getModelEntry(loc) ?: return emptyList()
        val raw = model.raw as? Model.Mcx ?: return emptyList()
        val parts = raw.data.parts.filter { filter(it.name) }
        val textureGrouped = parts.groupBy { it.texture }

        return textureGrouped.map {
            ModelCache { renderModelParts(raw.data, it.value) }.apply {
                texture = it.key.addPrefix("textures/").addPostfix(".png")
            }
        }
    }
}