package com.cout970.magneticraft.features.heat_machines

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.toCelsius
import com.cout970.magneticraft.misc.vector.lowercaseName
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.tilerenderers.*
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileCombustionChamber::class)
object TileRendererCombustionChamber : BaseTileRenderer<TileCombustionChamber>() {

    override fun init() {
        createModel(Blocks.combustionChamber,
            ModelSelector("door", FilterString("Door"))
        )
    }

    override fun render(te: TileCombustionChamber) {
        Utilities.rotateFromCenter(te.facing, 180f)
        renderModel("default")

        if (te.combustionChamberModule.doorOpen) {
            Utilities.customRotate(vec3Of(0, -135, 0), vec3Of(13.5 * PIXEL, 0.0, 0.5 * PIXEL))
        }
        renderModel("door")
    }
}

@RegisterRenderer(TileSteamBoiler::class)
object TileRendererSteamBoiler : BaseTileRenderer<TileSteamBoiler>() {

    override fun init() {
        createModel(Blocks.steamBoiler)
    }

    override fun render(te: TileSteamBoiler) {
        renderModel("default")
    }
}

@RegisterRenderer(TileHeatPipe::class)
object TileRendererHeatPipe : BaseTileRenderer<TileHeatPipe>() {

    override fun init() {
        val parts = listOf("up", "down", "north", "south", "west", "east")
        createModel(Blocks.heatPipe, parts.map { ModelSelector(it, FilterRegex(it)) })
    }

    override fun render(te: TileHeatPipe) {
        Utilities.withHeatColor(te, te.heatNode.temperature) {
            renderModel("default")

            EnumFacing.values().forEach { side ->
                if (te.heatPipeConnections.canConnect(side)) {
                    renderModel(side.lowercaseName)
                }
            }
        }
        if (Debug.DEBUG) {
            Utilities.renderFloatingLabel(te.heatNode.temperature.toCelsius().toInt().toString(), vec3Of(0, 1.25, 0))
        }
    }
}

@RegisterRenderer(TileInsulatedHeatPipe::class)
object TileRendererInsulatedHeatPipe : BaseTileRenderer<TileInsulatedHeatPipe>() {

    override fun init() {
        val parts = listOf("up", "down", "north", "south", "west", "east")
        createModel(Blocks.insulatedHeatPipe, parts.map { ModelSelector(it, FilterRegex(it)) })
        createModelWithoutTexture(Blocks.insulatedHeatPipe, ModelSelector("base", FilterRegex("center")))
    }

    override fun render(te: TileInsulatedHeatPipe) {

        var conn: EnumFacing? = null
        var count = 0

        EnumFacing.values().forEach { side ->
            if (te.heatPipeConnections.canConnect(side)) {
                renderModel(side.lowercaseName)
                conn = conn ?: side.opposite
                count++
            }
        }

        if (count == 1) {
            bindTexture(resource("textures/blocks/fluid_machines/insulated_heat_pipe_center_${conn!!.name.toLowerCase()}.png"))
        } else {
            bindTexture(resource("textures/blocks/fluid_machines/insulated_heat_pipe_center.png"))
        }
        renderModel("base")
    }
}

@RegisterRenderer(TileHeatSink::class)
object TileRendererHeatSink : BaseTileRenderer<TileHeatSink>() {

    override fun init() {
        createModel(Blocks.heatSink)
    }

    override fun render(te: TileHeatSink) {
        Utilities.facingRotate(te.facing)
        Utilities.withHeatColor(te, te.heatNode.temperature) {
            renderModel("default")
        }
    }
}

@RegisterRenderer(TileGasificationUnit::class)
object TileRendererGasificationUnit : BaseTileRenderer<TileGasificationUnit>() {

    override fun init() {
        createModel(Blocks.gasificationUnit)
    }

    override fun render(te: TileGasificationUnit) {
        Utilities.withHeatColor(te, te.heatNode.temperature) {
            renderModel("default")
        }
    }
}
