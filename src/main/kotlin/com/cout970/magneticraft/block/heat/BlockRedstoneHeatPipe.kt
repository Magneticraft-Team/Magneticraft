package com.cout970.magneticraft.block.heat

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.block.PROPERTY_OPEN
import com.cout970.magneticraft.tileentity.electric.TileRedstoneHeatPipe
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.cout970.magneticraft.util.get
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockRedstoneHeatPipe : BlockHeatMultistate(Material.ROCK, "redstone_heat_pipe"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileRedstoneHeatPipe()

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        worldIn?.setBlockState(pos, defaultState.withProperty(PROPERTY_OPEN, false))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        if (PROPERTY_OPEN[state]) return 1 else return 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.
            withProperty(PROPERTY_OPEN, (meta and 1) != 0)

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, PROPERTY_OPEN)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block) {
        if (!worldIn.isRemote) {
            val flag = worldIn.isBlockPowered(pos)
            if (flag || blockIn.defaultState.canProvidePower()) {
                if (PROPERTY_OPEN[state] != flag) {
                    worldIn.setBlockState(pos, defaultState.withProperty(PROPERTY_OPEN, flag))
                    val tile = worldIn.getTile<TileRedstoneHeatPipe>(pos)
                    tile?.heat?.conductivity = if (flag) 0.0 else DEFAULT_CONDUCTIVITY
                }
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn)
    }
}