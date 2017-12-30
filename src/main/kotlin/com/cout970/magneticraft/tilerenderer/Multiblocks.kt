@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.multiblock.MultiblockGrinder
import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.multiblock.MultiblockSteamEngine
import com.cout970.magneticraft.tileentity.TileGrinder
import com.cout970.magneticraft.tileentity.TileShelvingUnit
import com.cout970.magneticraft.tileentity.TileSolarPanel
import com.cout970.magneticraft.tileentity.TileSteamEngine
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

@RegisterRenderer(TileSolarPanel::class)
object TileRendererSolarPanel : TileRendererSimple<TileSolarPanel>(
        modelLocation = { ModelResourceLocation(Multiblocks.solarPanel.registryName, "model") },
        filters = addInverse(
                { it in TileRendererSolarPanel.left },
                { it in TileRendererSolarPanel.right }
        )
) {
    val left = listOf(
            "Panel1-1", "Panel1-2", "Panel1-3",
            "Panel2-1", "Panel2-2", "Panel2-3",
            "Panel3-1", "Panel3-2", "Panel3-3")

    val right = listOf(
            "Panel4-1", "Panel4-2", "Panel4-3",
            "Panel5-1", "Panel5-2", "Panel5-3",
            "Panel6-1", "Panel6-2", "Panel6-3")

    override fun renderModels(models: List<ModelCache>, te: TileSolarPanel) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSolarPanel)
            return
        }

        Utilities.rotateFromCenter(te.facing, -90f)
        translate(-1.0, 0.0, 0.0)
        models[0].renderTextured()

        val targetAngle = when (te.facing) {
            EnumFacing.NORTH -> {
                val time = (te.world.worldTime % 24000L).toInt()
                val normTime = time / 12000f
                if (normTime > 1) 0f else (normTime * 2 - 1) * 30f
            }
            EnumFacing.SOUTH -> {
                val time = (te.world.worldTime % 24000L).toInt()
                val normTime = time / 12000f
                if (normTime > 1) 0f else (normTime * 2 - 1) * -30f
            }
            else -> 0f
        }

        val oldTime = te.deltaTime
        te.deltaTime = System.currentTimeMillis()
        val delta = (Math.min(te.deltaTime - oldTime, 1000)) / 1000f

        val angle = te.currentAngle
        te.currentAngle += (targetAngle - te.currentAngle) * 0.5f * delta

        stackMatrix {
            translate(0f, 0.75f, 1.25f)
            rotate(angle, 1f, 0f, 0f)
            translate(0f, -0.75f + 0.1f, -1.25f)
            models[1].renderTextured()
        }
        stackMatrix {
            translate(0f, 0.75f, -0.25f)
            rotate(angle, 1f, 0f, 0f)
            translate(0f, -0.75f + 0.1f, 0.25f)
            models[2].renderTextured()
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
