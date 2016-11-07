package com.cout970.magneticraft.block.heat

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.TileHeatPipe
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockHeatPipe : BlockHeatMultistate(Material.ROCK, "heat_pipe"), ITileEntityProvider {

    override fun isVisuallyOpaque(): Boolean = false

    override fun isOpaqueCube(state: IBlockState?): Boolean = false

    override fun onNeighborChange(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        super.onNeighborChange(world, pos, neighbor)
        if (pos == null || neighbor == null) return
        val tile = world?.getTile<TileHeatPipe>(pos) ?: return
        val neighborTile = world?.getTileEntity(neighbor) ?: return
        val handler = NODE_HANDLER!!.fromTile(neighborTile) ?: return
        val facing = EnumFacing.getFacingFromVector((neighbor.x - pos.x).toFloat(), (neighbor.y - pos.y).toFloat(), (neighbor.z - pos.z).toFloat())
        if (handler is IHeatHandler)
            tile.activeSides.add(facing)
        else
            tile.activeSides.remove(facing)
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val tile = worldIn?.getTile<TileHeatPipe>(pos) ?: return
        for (i in EnumFacing.values()) {
            val neighborTile = worldIn?.getTileEntity(pos.offset(i)) ?: continue
            val handler = NODE_HANDLER!!.fromTile(neighborTile) ?: continue
            if (handler is IHeatHandler)
                tile.activeSides.add(i)
        }
    }

    override fun getActualState(state: IBlockState?, worldIn: IBlockAccess?, pos: BlockPos?): IBlockState {
        if (state == null) return defaultState
        if (pos == null) return state
        val tile = worldIn?.getTile<TileHeatPipe>(pos) ?: return state
//      for(i in EnumFacing.VALUES) {
//          if (tile.activeSides.contains(i))
//              state.withProperty(PropertyDirections.get(i), true)
//          else
//              state.withProperty(PropertyDirections.get(i), false)
//      }
        return state.withProperty(PROPERTY_NORTH, tile.activeSides.contains(EnumFacing.NORTH)).//TODO: HACK
                withProperty(PROPERTY_SOUTH, tile.activeSides.contains(EnumFacing.SOUTH)).
                withProperty(PROPERTY_EAST, tile.activeSides.contains(EnumFacing.EAST)).
                withProperty(PROPERTY_WEST, tile.activeSides.contains(EnumFacing.WEST)).
                withProperty(PROPERTY_UP, tile.activeSides.contains(EnumFacing.UP)).
                withProperty(PROPERTY_DOWN, tile.activeSides.contains(EnumFacing.DOWN))
    }

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_NORTH, PROPERTY_SOUTH, PROPERTY_EAST, PROPERTY_WEST, PROPERTY_UP, PROPERTY_DOWN)

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileHeatPipe()

}