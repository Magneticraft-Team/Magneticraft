package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.api.heat.IHeatNodeHandler
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by cout970 on 2017/02/26.
 */

fun IBlockAccess.getHeatHandler(pos: BlockPos): IHeatNodeHandler? {
    val tile = getTileEntity(pos) ?: return null
    val nodeHandler = HEAT_NODE_HANDLER!!.fromTile(tile)
    if(nodeHandler is IHeatNodeHandler){
        return nodeHandler
    }
    return null
}