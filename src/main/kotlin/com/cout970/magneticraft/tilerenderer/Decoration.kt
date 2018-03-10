package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Decoration
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileTubeLight
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.tilerenderer.core.modelOf

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileTubeLight::class)
object TileRendererTubeLight : TileRendererSimple<TileTubeLight>(
        modelLocation = modelOf(Decoration.tubeLight),
        filters = listOf(
                { it: String -> !it.matches("Right\\d".toRegex()) && !it.matches("Left\\d".toRegex()) },
                { it: String -> it.matches("Left\\d".toRegex()) },
                { it: String -> it.matches("Right\\d".toRegex()) }
        )
) {

    override fun renderModels(models: List<ModelCache>, te: TileTubeLight) {
        Utilities.rotateFromCenter(te.facing, 90f)
        val front = te.world.getTile<TileTubeLight>(te.pos.offset(te.facing, 1))
        val back = te.world.getTile<TileTubeLight>(te.pos.offset(te.facing, -1))

        models[0].renderTextured()
        if (front == null || front.facing.axis != te.facing.axis) {
            models[1].renderTextured()
        }
        if (back == null || back.facing.axis != te.facing.axis) {
            models[2].renderTextured()
        }
    }
}