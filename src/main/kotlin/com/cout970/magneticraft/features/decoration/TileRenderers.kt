package com.cout970.magneticraft.features.decoration

import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.systems.tilerenderers.BaseTileRenderer
import com.cout970.magneticraft.systems.tilerenderers.FilterRegex
import com.cout970.magneticraft.systems.tilerenderers.ModelSelector
import com.cout970.magneticraft.systems.tilerenderers.Utilities

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileTubeLight::class)
object TileRendererTubeLight : BaseTileRenderer<TileTubeLight>() {

    override fun init() {
        createModel(Blocks.tubeLight,
            ModelSelector("left", FilterRegex("Left\\d")),
            ModelSelector("right", FilterRegex("Right\\d"))
        )
    }

    override fun render(te: TileTubeLight) {
        Utilities.rotateFromCenter(te.facing, 90f)
        val front = te.world.getTile<TileTubeLight>(te.pos.offset(te.facing, 1))
        val back = te.world.getTile<TileTubeLight>(te.pos.offset(te.facing, -1))

        renderModel("default")
        if (front == null || front.facing.axis != te.facing.axis) {
            renderModel("left")
        }
        if (back == null || back.facing.axis != te.facing.axis) {
            renderModel("right")
        }
    }
}