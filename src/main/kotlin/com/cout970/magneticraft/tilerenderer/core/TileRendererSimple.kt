package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.tileentity.core.TileBase
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/07/01.
 */
abstract class TileRendererSimple<T : TileBase>(
        val modelLocation: (() -> ModelResourceLocation)?,
        val filters: List<(String) -> Boolean> = listOf({ _ -> true })
) : TileRenderer<T>() {

    var caches = listOf<ModelCache>()
    var ticks: Float = 0.0f

    override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        // error loading the model
        if (caches.isEmpty()) return
        stackMatrix {
            translate(x, y, z)
            ticks = partialTicks
            renderModels(caches, te)
        }
    }

    companion object {

        fun filterOf(names: List<String>): List<(String) -> Boolean> {
            return listOf({ name: String -> name !in names }) + names.map { name -> { it: String -> it == name } }
        }

        fun filtersOf(vararg names: List<String>): List<(String) -> Boolean> {
            return listOf({ name: String -> names.none { name in it } }) +
                   names.flatMap { it.map { name -> { it: String -> it == name } } }
        }

        fun addInverse(vararg list: (String) -> Boolean): List<(String) -> Boolean> {
            return listOf({ s: String -> list.none { it.invoke(s) } }) + list.toList()
        }
    }

    abstract fun renderModels(models: List<ModelCache>, te: T)

    fun ModelCache.renderTextured() {
        bindTexture(texture)
        render()
    }

    override fun onModelRegistryReload() {
        val loc = modelLocation?.invoke() ?: return
        //cleaning
        caches.forEach { it.close() }
        caches = filters.flatMap { ModelCacheFactory.createMultiTextureCache(loc, it) }
    }
}