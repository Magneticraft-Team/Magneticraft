package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Decoration
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileTubeLight
import com.cout970.magneticraft.tilerenderer.core.BaseTileRenderer
import com.cout970.magneticraft.tilerenderer.core.FilterRegex
import com.cout970.magneticraft.tilerenderer.core.ModelSelector
import com.cout970.magneticraft.tilerenderer.core.Utilities

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileTubeLight::class)
object TileRendererTubeLight : BaseTileRenderer<TileTubeLight>() {

    override fun init() {
        createModel(Decoration.tubeLight,
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