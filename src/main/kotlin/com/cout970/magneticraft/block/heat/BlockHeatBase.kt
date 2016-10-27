package com.cout970.magneticraft.block.heat

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.tileentity.electric.TileElectricHeatBase
import com.cout970.magneticraft.tileentity.electric.TileHeatBase
import com.cout970.magneticraft.tileentity.electric.TileHeatSink
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class BlockHeatBase(material: Material, name: String) : BlockMultiState(material, name), ITileEntityProvider {

    override fun getMetaFromState(state: IBlockState): Int = 0

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this)

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val tile = worldIn?.getTile<TileHeatSink>(pos) ?: return
        tile.updateHeatConnections()
    }

    override fun randomTick(worldIn: World?, pos: BlockPos?, state: IBlockState?, random: Random?) {
        super.randomTick(worldIn, pos, state, random)
        if (pos == null) return
        if (worldIn == null) return
        val tile = worldIn.getTile<TileHeatBase>(pos)
        if (tile != null) {
            lightValue = (15f * tile.lightLevelCache).toInt()
            return
        }
        val tileE = worldIn.getTile<TileElectricHeatBase>(pos)  //TODO: Make this not a hack
        if (tileE != null) {
            lightValue = (15f * tileE.lightLevelCache).toInt()
        } 
    }

    override fun tickRate(worldIn: World?): Int {
        return 1
    }

    override fun onNeighborChange(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        super.onNeighborChange(world, pos, neighbor)
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