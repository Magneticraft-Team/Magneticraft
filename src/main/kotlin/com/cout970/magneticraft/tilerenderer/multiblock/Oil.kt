package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.multiblock.TileOilHeater
import com.cout970.magneticraft.tileentity.multiblock.TilePumpjack
import com.cout970.magneticraft.tileentity.multiblock.TileRefinery
import com.cout970.magneticraft.tilerenderer.core.Utilities

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