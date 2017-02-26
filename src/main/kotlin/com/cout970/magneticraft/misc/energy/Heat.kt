package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by cout970 on 2017/02/26.
 */

fun IBlockAccess.getHeatHandler(pos: BlockPos): IHeatHandler? {
    val tile = getTileEntity(pos) ?: return null
    val nodeHandler = NODE_HANDLER!!.fromTile(tile)
    if(nodeHandler is IHeatHandler){
        return nodeHandler
    }
    return null
}