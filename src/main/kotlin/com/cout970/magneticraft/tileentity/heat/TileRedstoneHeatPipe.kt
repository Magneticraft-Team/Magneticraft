package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 04/07/2016.
 */
class TileRedstoneHeatPipe() : TileHeatBase() {

    val activeSides: MutableSet<EnumFacing> = mutableSetOf()

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3 / 8,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT / 8).toLong(),
            conductivity = DEFAULT_CONDUCTIVITY,
            tile = this)

    override fun onLoad() {
        super.onLoad()
        //val block = world.getBlock<BlockHeatPipe>(pos) ?: return
    }

    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)
}