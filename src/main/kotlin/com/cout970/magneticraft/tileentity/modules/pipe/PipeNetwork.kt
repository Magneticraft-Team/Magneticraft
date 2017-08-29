package com.cout970.magneticraft.tileentity.modules.pipe

import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModulePipe

/**
 * Created by cout970 on 2017/08/28.
 */

class PipeNetwork(module: ModulePipe) : Network<ModulePipe>(
        module,
        getInspectFunc(module.type),
        Companion::createNetwork
) {

    companion object {

        fun getInspectFunc(type: PipeType): InspectFunc {
            return func@ { tile, side ->
                (tile as? TileBase)?.getModule<ModulePipe>()?.let {
                    if (it.type == type) listOf(it) else null
                } ?: emptyList()
            }
        }

        fun createNetwork(mod: ModulePipe): PipeNetwork {
            return PipeNetwork(mod)
        }
    }
}

enum class PipeType(val maxRate: Int) {
    COPPER(80),
    IRON(160),
    STEEL(320),
    TUNGSTEN(640)
}