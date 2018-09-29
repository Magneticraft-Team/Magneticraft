package com.cout970.magneticraft.systems.tilemodules.pipe

import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModulePipe

/**
 * Created by cout970 on 2017/08/28.
 */

class PipeNetwork(module: ModulePipe) : Network<ModulePipe>(
    module,
    getInspectFunc(module.type),
    Companion::createNetwork
) {

    override fun clearCache() {}

    companion object {

        fun getInspectFunc(type: PipeType): InspectFunc {
            return func@{ tile, _ ->
                val tileBase = tile as? TileBase ?: return@func emptyList()
                if (tile.isInvalid) return@func emptyList()

                val module = tileBase.getModule<ModulePipe>() ?: return@func emptyList()
                if (module.type == type) listOf(module) else emptyList()
            }
        }

        fun createNetwork(mod: ModulePipe): PipeNetwork = PipeNetwork(mod)
    }
}

enum class PipeType(val maxRate: Int) {
    IRON(160),
    STEEL(320),
    TUNGSTEN(640)
}