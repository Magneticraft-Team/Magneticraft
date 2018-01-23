@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.multiblock.*
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */
private fun genNames(prefix: String): List<String> = (1..3).map { "$prefix-$it" }

@RegisterRenderer(TileSolarPanel::class)
object TileRendererSolarPanel : TileRendererSimple<TileSolarPanel>(
        modelLocation = { ModelResourceLocation(Multiblocks.solarPanel.registryName, "model") },
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
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSolarPanel)
            return
        }

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

@RegisterRenderer(TileShelvingUnit::class)
object TileRendererShelvingUnit : TileRendererSimple<TileShelvingUnit>(
        modelLocation = { ModelResourceLocation(Multiblocks.shelvingUnit.registryName, "model") },
        filters = filterOf((1..24).map { "Crate$it" })
) {

    override fun renderModels(models: List<ModelCache>, te: TileShelvingUnit) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockShelvingUnit)
            return
        }

        Utilities.rotateFromCenter(te.facing, 0f)
        models[0].renderTextured()
        te.shelvingUnitModule.chestCount.forEachIndexed { level, count ->
            (0 until count).forEach {
                models[1 + level * ModuleShelvingUnitMb.CHESTS_PER_LEVEL + it].renderTextured()
            }
        }
    }
}

@RegisterRenderer(TileSteamEngine::class)
object TileRendererSteamEngine : TileRendererSimple<TileSteamEngine>(
        modelLocation = { ModelResourceLocation(Multiblocks.steamEngine.registryName, "model") },
        filters = listOf<(String) -> Boolean>(
                { it !in TileRendererSteamEngine.partsNames },
                { it in TileRendererSteamEngine.partsNames }
        )
) {

    val partsNames = listOf("gearbox_lid_side", "gear_box_lid_top", "gearbox_lid_lock")

    override fun renderModels(models: List<ModelCache>, te: TileSteamEngine) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSteamEngine)
            return
        }

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

@RegisterRenderer(TileGrinder::class)
object TileRendererGrinder : TileRendererSimple<TileGrinder>(
        modelLocation = { ModelResourceLocation(Multiblocks.grinder.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileGrinder) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockGrinder)
            return
        }
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        models[0].renderTextured()
    }
}

@RegisterRenderer(TileSieve::class)
object TileRendererSieve : TileRendererSimple<TileSieve>(
        modelLocation = { ModelResourceLocation(Multiblocks.sieve.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileSieve) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSieve)
            return
        }
        Utilities.rotateFromCenter(te.facing, 180f)
        translate(0, 0, 2)
        models[0].renderTextured()
    }
}
