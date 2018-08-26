package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.tileentity.multiblock.TileContainer
import com.cout970.magneticraft.tileentity.multiblock.TileShelvingUnit
import com.cout970.magneticraft.tilerenderer.core.FilterString
import com.cout970.magneticraft.tilerenderer.core.ModelSelector
import com.cout970.magneticraft.tilerenderer.core.Utilities

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