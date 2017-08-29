package com.cout970.magneticraft.misc.fluid

import net.minecraftforge.fluids.capability.IFluidHandler

/**
 * Created by cout970 on 2017/08/29.
 */

fun IFluidHandler.fillFromTank(max: Int, tank: IFluidHandler): Int {
    val canDrain = tank.drain(max, false)
    if (canDrain != null) {
        val result = fill(canDrain, false)
        if (result > 0) {
            val drained = tank.drain(result, true)
            return fill(drained, true)
        }
    }
    return 0
}

fun IFluidHandler.isEmpty(): Boolean {
    return tankProperties.none { it.contents != null }
}