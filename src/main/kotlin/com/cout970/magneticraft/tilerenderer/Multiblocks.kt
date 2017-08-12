package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.multiblock.MultiblockSteamEngine
import com.cout970.magneticraft.tileentity.TileShelvingUnit
import com.cout970.magneticraft.tileentity.TileSolarPanel
import com.cout970.magneticraft.tileentity.TileSteamEngine
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileSolarPanel::class)
object TileRendererSolarPanel : TileRendererSimple<TileSolarPanel>(
        modelLocation = { ModelResourceLocation(Multiblocks.solarPanel.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileSolarPanel) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSolarPanel)
            return
        }

        Utilities.rotateFromCenter(te.facing, -90f)
        translate(-1.0, 0.0, 0.0)
        models.forEach { it.renderTextured() }
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

        translate(0, -2 * PIXEL * clock, 0)
        translate(-4 * PIXEL, 1, 0)
        rotate(-120 * clock, 0, 0, 1)
        translate(4 * PIXEL, -1, 0)
        models[1].renderTextured()
    }
}