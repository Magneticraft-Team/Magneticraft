package com.cout970.magneticraft.block.heat

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.block.BlockBase
import com.cout970.magneticraft.tileentity.electric.TileInfiniteHeat
import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockInfiniteHeat : BlockBase(Material.ROCK, "infinite_heat"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileInfiniteHeat(1800.toKelvinFromCelsius())

    override fun onNeighborChange(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        super.onNeighborChange(world, pos, neighbor)
        if (pos == null) return
        val tile = world?.getTile<TileInfiniteHeat>(pos) ?: return
        tile.updateHeatConnections()
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val tile = worldIn?.getTile<TileInfiniteHeat>(pos) ?: return
        tile.updateHeatConnections()
    }
}