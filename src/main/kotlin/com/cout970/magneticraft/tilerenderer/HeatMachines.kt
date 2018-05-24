package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.block.HeatMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.TileCombustionChamber
import com.cout970.magneticraft.tileentity.TileHeatPipe
import com.cout970.magneticraft.tileentity.TileHeatSink
import com.cout970.magneticraft.tileentity.TileSteamBoiler
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.toCelsius
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.util.EnumFacing

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

@RegisterRenderer(TileHeatPipe::class)
object TileRendererHeatPipe : TileRendererSimple<TileHeatPipe>(
        modelLocation = modelOf(HeatMachines.heatPipe),
        filters = filterOf(listOf("base", "up", "down", "north", "south", "west", "east"))
) {

    val sides = listOf(
            EnumFacing.UP to 1,
            EnumFacing.DOWN to 2,
            EnumFacing.NORTH to 3,
            EnumFacing.SOUTH to 4,
            EnumFacing.WEST to 5,
            EnumFacing.EAST to 6
    )

    override fun renderModels(models: List<ModelCache>, te: TileHeatPipe) {
        Utilities.withHeatColor(te, te.heatNode.temperature, vec3Of(0.5)) {
            models[0].renderTextured()

            sides.forEach {
                val tile = te.world.getTileEntity(te.pos + it.first) ?: return@forEach

                FLUID_HANDLER!!.fromTile(tile, it.first.opposite)?.let { _ ->
                    models[it.second].render()
                }

                if (tile.getOrNull(HEAT_NODE_HANDLER, it.first.opposite) != null) {
                    models[it.second].render()
                }
            }
        }
        if (Debug.DEBUG) {
//            Utilities.renderFloatingLabel("U: %.2f J".format(te.heatNode.internalEnergy), vec3Of(0, 1, 0))
//            Utilities.renderFloatingLabel("T: %.2f C".format(te.heatNode.temperature.toCelsius()), vec3Of(0, 1.25, 0))
            Utilities.renderFloatingLabel(te.heatNode.temperature.toCelsius().toInt().toString(), vec3Of(0, 1.25, 0))
        }
    }
}


@RegisterRenderer(TileHeatSink::class)
object TileRendererHeatSink : TileRendererSimple<TileHeatSink>(
        modelLocation = modelOf(HeatMachines.heatSink)
) {

    override fun renderModels(models: List<ModelCache>, te: TileHeatSink) {
        Utilities.facingRotate(te.facing)
        Utilities.withHeatColor(te, te.heatNode.temperature, vec3Of(0.5)) {
            models[0].renderTextured()
        }
    }
}
