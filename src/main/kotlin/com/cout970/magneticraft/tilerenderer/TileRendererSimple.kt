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
        val filters: List<(String) -> Boolean> = listOf({ _ -> true })
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

    companion object {
        fun filterOf(names: List<String>): List<(String) -> Boolean> {
            return listOf({ name: String -> name !in names }) + names.map { name -> { it: String -> it == name } }
        }
    }

    abstract fun renderModels(models: List<ModelCache>, te: T)

    fun ModelCache.renderTextured(){
        bindTexture(texture)
        render()
    }

    override fun onModelRegistryReload() {
        val loc = modelLocation()
        //cleaning
        caches.forEach { it.clear() }
        caches = filters.flatMap { ModelCacheFactory.createMultiTextureCache(loc, it) }
    }
}