package com.cout970.magneticraft.features.multiblocks.tilerenderers

import com.cout970.magneticraft.features.multiblocks.tileentities.TileContainer
import com.cout970.magneticraft.features.multiblocks.tileentities.TileShelvingUnit
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.systems.tilemodules.ModuleShelvingUnitMb
import com.cout970.magneticraft.systems.tilerenderers.FilterString
import com.cout970.magneticraft.systems.tilerenderers.ModelSelector
import com.cout970.magneticraft.systems.tilerenderers.Utilities
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

@RegisterRenderer(TileShelvingUnit::class)
object TileRendererShelvingUnit : TileRendererMultiblock<TileShelvingUnit>() {

    private val crateMap = (1..24).map { "crate-$it" }

    override fun init() {
        createModel(Multiblocks.shelvingUnit,
            (1..24).map { ModelSelector("crate-$it", FilterString("Crate$it")) }
        )
    }

    override fun render(te: TileShelvingUnit) {
        Utilities.rotateFromCenter(te.facing, 0f)
        renderModel("default")
        te.shelvingUnitModule.chestCount.forEachIndexed { level, count ->
            (0 until count).forEach {
                val crate = level * ModuleShelvingUnitMb.CHESTS_PER_LEVEL + it
                renderModel(crateMap[crate])
            }
        }
    }
}

@RegisterRenderer(TileContainer::class)
object TileRendererContainer : TileRendererMultiblock<TileContainer>() {

    override fun init() {
        createModel(Multiblocks.container)
    }

    override fun render(te: TileContainer) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -3)
        renderModel("default")
    }
}