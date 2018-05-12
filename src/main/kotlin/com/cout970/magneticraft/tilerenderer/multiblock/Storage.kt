package com.cout970.magneticraft.tilerenderer.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.tileentity.multiblock.TileContainer
import com.cout970.magneticraft.tileentity.multiblock.TileShelvingUnit
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.tilerenderer.core.modelOf

@RegisterRenderer(TileShelvingUnit::class)
object TileRendererShelvingUnit : TileRendererMultiblock<TileShelvingUnit>(
        modelLocation = modelOf(Multiblocks.shelvingUnit),
        filters = filterOf((1..24).map { "Crate$it" })
) {

    override fun renderModels(models: List<ModelCache>, te: TileShelvingUnit) {
        Utilities.rotateFromCenter(te.facing, 0f)
        models[0].renderTextured()
        te.shelvingUnitModule.chestCount.forEachIndexed { level, count ->
            (0 until count).forEach {
                models[1 + level * ModuleShelvingUnitMb.CHESTS_PER_LEVEL + it].renderTextured()
            }
        }
    }
}

@RegisterRenderer(TileContainer::class)
object TileRendererContainer : TileRendererMultiblock<TileContainer>(
        modelLocation = modelOf(Multiblocks.container)
) {

    override fun renderModels(models: List<ModelCache>, te: TileContainer) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -3)
        models[0].renderTextured()
    }
}