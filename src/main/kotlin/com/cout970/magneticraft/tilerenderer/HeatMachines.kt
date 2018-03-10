package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.HeatMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileCombustionChamber
import com.cout970.magneticraft.tileentity.TileSteamBoiler
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.vector.vec3Of

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileCombustionChamber::class)
object TileRendererCombustionChamber : TileRendererSimple<TileCombustionChamber>(
        modelLocation = modelOf(HeatMachines.combustionChamber),
        filters = listOf<(String) -> Boolean>({ it != "Door" }, { it == "Door" })
) {
    override fun renderModels(models: List<ModelCache>, te: TileCombustionChamber) {
        Utilities.rotateFromCenter(te.facing, 180f)
        models.take(models.size - 1).forEach { it.renderTextured() }
        if (te.combustionChamberModule.doorOpen) {
            Utilities.customRotate(vec3Of(0, -135, 0), vec3Of(13.5 * PIXEL, 0.0, 0.5 * PIXEL))
        }
        models.last().renderTextured()
    }
}

@RegisterRenderer(TileSteamBoiler::class)
object TileRendererSteamBoiler : TileRendererSimple<TileSteamBoiler>(
        modelLocation = modelOf(HeatMachines.steamBoiler)
) {
    override fun renderModels(models: List<ModelCache>, te: TileSteamBoiler) {
        models.forEach { it.renderTextured() }
    }
}