package com.cout970.magneticraft.features.multiblocks.tilerenderers

import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.systems.tilerenderers.*
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

@RegisterRenderer(TileGrinder::class)
object TileRendererGrinder : TileRendererMultiblock<TileGrinder>() {

    override fun init() {
        createModel(Multiblocks.grinder, ModelSelector("animation", FilterAlways, FilterRegex("animation", FilterTarget.ANIMATION)))
    }

    override fun render(te: TileGrinder) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        if (te.processModule.working) renderModel("animation") else renderModel("default")
    }
}

@RegisterRenderer(TileSieve::class)
object TileRendererSieve : TileRendererMultiblock<TileSieve>() {

    override fun init() {
        createModel(Multiblocks.sieve, ModelSelector("animation", FilterAlways, FilterRegex("animation", FilterTarget.ANIMATION)))
    }

    override fun render(te: TileSieve) {
        Utilities.rotateFromCenter(te.facing, 180f)
        translate(0, 0, 2)
        if (te.processModule.working) renderModel("animation") else renderModel("default")
    }
}

@RegisterRenderer(TileHydraulicPress::class)
object TileRendererHydraulicPress : TileRendererMultiblock<TileHydraulicPress>() {

    override fun init() {
        createModel(Multiblocks.hydraulicPress, ModelSelector("animation", FilterAlways, FilterRegex("animation", FilterTarget.ANIMATION)))
    }

    override fun render(te: TileHydraulicPress) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        if (te.processModule.working) renderModel("animation") else renderModel("default")
    }
}

@RegisterRenderer(TileBigCombustionChamber::class)
object TileRendererBigCombustionChamber : TileRendererMultiblock<TileBigCombustionChamber>() {

    override fun init() {
        createModel(Multiblocks.bigCombustionChamber,
            ModelSelector("fire_off", FilterString("fire_off")),
            ModelSelector("fire_on", FilterString("fire_on"))
        )
    }

    override fun render(te: TileBigCombustionChamber) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        renderModel("default")
        if (te.bigCombustionChamberModule.working()) renderModel("fire_on") else renderModel("fire_off")
    }
}

@RegisterRenderer(TileBigSteamBoiler::class)
object TileRendererBigSteamBoiler : TileRendererMultiblock<TileBigSteamBoiler>() {

    override fun init() {
        createModel(Multiblocks.bigSteamBoiler)
    }

    override fun render(te: TileBigSteamBoiler) {
        Utilities.rotateFromCenter(te.facing, 0f)
        renderModel("default")
    }
}

@RegisterRenderer(TileSteamTurbine::class)
object TileRendererSteamTurbine : TileRendererMultiblock<TileSteamTurbine>() {

    override fun init() {
        createModel(Multiblocks.steamTurbine,
            ModelSelector("blade", FilterRegex("blade.*")),
            ModelSelector("not_blade", FilterNotRegex("blade.*"))
        )
    }

    override fun render(te: TileSteamTurbine) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, 0)
        renderModel("not_blade")

        val speed = 0.25f * te.steamGeneratorModule.production.storage / te.steamGeneratorModule.maxProduction

        // Smooth changes in speed
        if (te.turbineSpeed < speed) {
            te.turbineSpeed += 0.25f / 20
        } else if (te.turbineSpeed > speed) {
            te.turbineSpeed -= 0.25f / 20
        }

        // Smooth rotation
        val now = (System.currentTimeMillis() % 0xFFFF).toFloat()
        val delta = now - te.lastTime

        te.lastTime = now
        te.turbineAngle += te.turbineSpeed * delta

        translate(1.5f, 1.5f, 0f)
        repeat(12) {
            stackMatrix {
                rotate(it * 360 / 12f + te.turbineAngle, 0, 0, 1)
                translate(-1.5f, -1.5f, 0f)
                renderModel("blade")
            }
        }
    }
}