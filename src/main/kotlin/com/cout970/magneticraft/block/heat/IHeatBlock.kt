package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.misc.tileentity.HeatHandler
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by cout970 on 04/07/2016.
 */
interface IHeatBlock {

    fun getHeatLightValue(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Int {
        if (pos == null) return 0
        val tile = world?.getTileEntity(pos) ?: return 0
        val handler = NODE_HANDLER!!.fromTile(tile) ?: return 0
        if (handler is HeatHandler) {
            return (15f * handler.lightLevel).toInt()
        }
        return 0
    }

    fun heatNeighborCheck(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        if (pos == null) return
        val tile: TileEntity = world?.getTileEntity(pos) ?: return
        val handler: INodeHandler = NODE_HANDLER!!.fromTile(tile) ?: return
        if (handler is HeatHandler) {
            handler.updateConnections()
        }
    }
}