package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileSieve
import com.cout970.magneticraft.tilerenderer.core.*

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