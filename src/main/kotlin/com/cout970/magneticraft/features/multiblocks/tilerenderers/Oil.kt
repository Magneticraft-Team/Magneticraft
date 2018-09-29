package com.cout970.magneticraft.features.multiblocks.tilerenderers

import com.cout970.magneticraft.features.multiblocks.tileentities.TileOilHeater
import com.cout970.magneticraft.features.multiblocks.tileentities.TilePumpjack
import com.cout970.magneticraft.features.multiblocks.tileentities.TileRefinery
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.systems.tilerenderers.Utilities
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

@RegisterRenderer(TilePumpjack::class)
object TileRendererPumpjack : TileRendererMultiblock<TilePumpjack>() {

    override fun init() {
        createModel(Multiblocks.pumpjack)
    }

    override fun render(te: TilePumpjack) {
        Utilities.rotateFromCenter(te.facing, 90f)
        translate(1, 0, 0)
        renderModel("default")
    }
}


@RegisterRenderer(TileOilHeater::class)
object TileRendererOilHeater : TileRendererMultiblock<TileOilHeater>() {

    override fun init() {
        createModel(Multiblocks.oilHeater)
    }

    override fun render(te: TileOilHeater) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, 0)
        renderModel("default")
    }
}

@RegisterRenderer(TileRefinery::class)
object TileRendererRefinery : TileRendererMultiblock<TileRefinery>() {

    override fun init() {
        createModel(Multiblocks.refinery)
    }

    override fun render(te: TileRefinery) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        renderModel("default")
    }
}