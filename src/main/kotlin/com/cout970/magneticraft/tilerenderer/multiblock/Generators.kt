package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.multiblock.TileSolarPanel
import com.cout970.magneticraft.tileentity.multiblock.TileSteamEngine
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.tilerenderer.core.modelOf
import net.minecraft.util.EnumFacing

@RegisterRenderer(TileSteamEngine::class)
object TileRendererSteamEngine : TileRendererMultiblock<TileSteamEngine>(
        modelLocation = modelOf(Multiblocks.steamEngine),
        filters = listOf<(String) -> Boolean>(
                { it !in TileRendererSteamEngine.partsNames },
                { it in TileRendererSteamEngine.partsNames }
        )
) {

    val partsNames = listOf("gearbox_lid_side", "gear_box_lid_top", "gearbox_lid_lock")

    override fun renderModels(models: List<ModelCache>, te: TileSteamEngine) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, -1)
        models[0].renderTextured()

        val step = Math.max(0.0, (te.steamEngineMbModule.auxTime - ticks) / 20.0)
        val clock = if (te.steamEngineMbModule.lidOpen) 1 - step else step

        translate(-0.5 * PIXEL * clock, -2.5 * PIXEL * clock, 0)
        translate(-4 * PIXEL, 1, 0)
        rotate(-120 * clock, 0, 0, 1)
        translate(4 * PIXEL, -1, 0)
        models[1].renderTextured()
    }
}

@RegisterRenderer(TileSolarPanel::class)
object TileRendererSolarPanel : TileRendererMultiblock<TileSolarPanel>(
        modelLocation = modelOf(Multiblocks.solarPanel),
        filters = filtersOf(
                genNames("Panel1"),
                genNames("Panel2"),
                genNames("Panel3"),
                genNames("Panel4"),
                genNames("Panel5"),
                genNames("Panel6")
        )
) {

    override fun renderModels(models: List<ModelCache>, te: TileSolarPanel) {
        Utilities.rotateFromCenter(te.facing, -90f)
        translate(-1.0, 0.0, 0.0)
        models[0].renderTextured()

        val worldTime = te.world.worldTime
        val time = (worldTime % 24000L).toInt()
        val normTime = time / 12000f
        val preAngle = if (normTime > 1) 0f else (normTime * 2 - 1) * 30f

        val targetAngle = when (te.facing) {
            EnumFacing.NORTH -> preAngle
            EnumFacing.SOUTH -> -preAngle
            EnumFacing.WEST -> preAngle
            EnumFacing.EAST -> -preAngle
            else -> 0f
        }

        val oldTime = te.deltaTime
        te.deltaTime = System.currentTimeMillis()
        val delta = (Math.min(te.deltaTime - oldTime, 1000)) / 1000f

        val angle = te.currentAngle
        te.currentAngle += (targetAngle - te.currentAngle) * delta * 0.5f

        when (te.facing.axis) {
            EnumFacing.Axis.X -> {
                stackMatrix {
                    translate(1.5f, 11f / 16f, 0.5f)
                    rotate(angle, 0f, 0f, 1f)
                    translate(-1.5f, -11f / 16f, -0.5f)
                    (1..3).forEach { models[it].renderTextured() }
                    (10..12).forEach { models[it].renderTextured() }
                }
                stackMatrix {
                    translate(0.5f, 11f / 16f, 0.5f)
                    rotate(angle, 0f, 0f, 1f)
                    translate(-0.5f, -11f / 16f, -0.5f)
                    (4..6).forEach { models[it].renderTextured() }
                    (13..15).forEach { models[it].renderTextured() }
                }
                stackMatrix {
                    translate(-0.5f, 11f / 16f, 0.5f)
                    rotate(angle, 0f, 0f, 1f)
                    translate(0.5f, -11f / 16f, -0.5f)
                    (7..9).forEach { models[it].renderTextured() }
                    (16..18).forEach { models[it].renderTextured() }
                }
            }
            EnumFacing.Axis.Z -> {
                stackMatrix {
                    translate(0f, 0.75f, 1.25f)
                    rotate(angle, 1f, 0f, 0f)
                    translate(0f, -0.75f + 0.1f, -1.25f)
                    (1..9).forEach { models[it].renderTextured() }
                }
                stackMatrix {
                    translate(0f, 0.75f, -0.25f)
                    rotate(angle, 1f, 0f, 0f)
                    translate(0f, -0.75f + 0.1f, 0.25f)
                    (10..18).forEach { models[it].renderTextured() }
                }
            }
            else -> Unit
        }
    }
}