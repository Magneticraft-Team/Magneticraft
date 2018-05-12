@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.tileentity.multiblock.TileMultiblock
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/08/10.
 */
internal fun genNames(prefix: String): List<String> = (1..3).map { "$prefix-$it" }

abstract class TileRendererMultiblock<T : TileMultiblock>(
        modelLocation: (() -> ModelResourceLocation)?,
        filters: List<(String) -> Boolean> = listOf({ _ -> true })
) : TileRendererSimple<T>(modelLocation, filters) {

    override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        // error loading the model
        if (caches.isEmpty()) return
        stackMatrix {
            translate(x, y, z)
            ticks = partialTicks

            if (!te.active) {
                Utilities.multiblockPreview(te.multiblockContext())
            } else {
                renderModels(caches, te)
            }
        }
    }

    override fun isGlobalRenderer(te: T): Boolean = true
}



