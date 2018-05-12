package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.multiblock.TileOilHeater
import com.cout970.magneticraft.tileentity.multiblock.TilePumpjack
import com.cout970.magneticraft.tileentity.multiblock.TileRefinery
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.tilerenderer.core.modelOf

@RegisterRenderer(TilePumpjack::class)
object TileRendererPumpjack : TileRendererMultiblock<TilePumpjack>(
        modelLocation = modelOf(Multiblocks.pumpjack)
) {

    override fun renderModels(models: List<ModelCache>, te: TilePumpjack) {
        Utilities.rotateFromCenter(te.facing, 90f)
        translate(1, 0, 0)
        models[0].renderTextured()
    }
}


@RegisterRenderer(TileOilHeater::class)
object TileRendererOilHeater : TileRendererMultiblock<TileOilHeater>(
        modelLocation = modelOf(Multiblocks.oilHeater)
) {

    override fun renderModels(models: List<ModelCache>, te: TileOilHeater) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, 0)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileRefinery::class)
object TileRendererRefinery : TileRendererMultiblock<TileRefinery>(
        modelLocation = modelOf(Multiblocks.refinery)
) {

    override fun renderModels(models: List<ModelCache>, te: TileRefinery) {
        Utilities.rotateFromCenter(te.facing, 180f)
        translate(-1, 0, 2)
        models.forEach { it.renderTextured() }
    }
}