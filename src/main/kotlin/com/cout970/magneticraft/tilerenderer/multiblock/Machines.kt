package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileSieve
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.tilerenderer.core.modelOf

@RegisterRenderer(TileGrinder::class)
object TileRendererGrinder : TileRendererMultiblock<TileGrinder>(
        modelLocation = modelOf(Multiblocks.grinder)
) {

    override fun renderModels(models: List<ModelCache>, te: TileGrinder) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        models[0].renderTextured()
    }
}

@RegisterRenderer(TileSieve::class)
object TileRendererSieve : TileRendererMultiblock<TileSieve>(
        modelLocation = modelOf(Multiblocks.sieve)
) {

    override fun renderModels(models: List<ModelCache>, te: TileSieve) {
        Utilities.rotateFromCenter(te.facing, 180f)
        translate(0, 0, 2)
        models[0].renderTextured()
    }
}

@RegisterRenderer(TileHydraulicPress::class)
object TileRendererHydraulicPress : TileRendererMultiblock<TileHydraulicPress>(
        modelLocation = modelOf(Multiblocks.hydraulicPress)
) {

    override fun renderModels(models: List<ModelCache>, te: TileHydraulicPress) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        models.forEach { it.renderTextured() }
    }
}