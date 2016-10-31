package com.cout970.magneticraft.block.heat

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.tileentity.electric.TileElectricHeatBase
import com.cout970.magneticraft.tileentity.electric.TileHeatBase
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by cout970 on 04/07/2016.
 */
interface IHeatBlock {

    fun getHeatLightValue(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Int {
        if (pos == null) return 0
        if (world == null) return 0
        val tile = world.getTile<TileHeatBase>(pos)
        if (tile != null) {
            return (15f * tile.lightLevelCache).toInt()
        }
        val tileE = world.getTile<TileElectricHeatBase>(pos)  //TODO: Make this not a hack
        if (tileE != null) {
            return (15f * tileE.lightLevelCache).toInt()
        }
        return 0
    }

    fun heatNeighborCheck(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        if (pos == null) return
        if (world == null) return
        val tile = world.getTile<TileHeatBase>(pos)
        if (tile != null) {
            tile.updateHeatConnections()
            return
        }
        val tileE = world.getTile<TileElectricHeatBase>(pos)  //TODO: Make this not a hack
        if (tileE != null) {
            tileE.updateHeatConnections()
        }
    }
}