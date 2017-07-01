package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/07/01.
 */
abstract class TileRendererSimple<T : TileBase>(
        val modelLocation: () -> ModelResourceLocation,
        val filters: List<(String) -> Boolean> = listOf({ it -> true })
) : TileRenderer<T>() {

    var caches = listOf<ModelCache>()

    override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        // error loading the model
        if (caches.isEmpty()) return
        stackMatrix {
            translate(x, y, z)
            renderModels(caches, te)
        }
    }

    abstract fun renderModels(models: List<ModelCache>, te: T)

    override fun onModelRegistryReload() {
        val loc = modelLocation()
        //cleaning
        caches.forEach { it.clear() }
        caches = filters.mapNotNull { ModelCacheFactory.createCache(loc, it) }
    }
}