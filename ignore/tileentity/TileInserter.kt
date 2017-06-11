package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 25/08/2016.
 */
class TileInserter : TileBase() {

    fun getDirection(): EnumFacing {
        val state = getBlockState()
        if (PROPERTY_DIRECTION.isIn(state)) {
            return state[PROPERTY_DIRECTION]
        }
        return EnumFacing.NORTH
    }
}