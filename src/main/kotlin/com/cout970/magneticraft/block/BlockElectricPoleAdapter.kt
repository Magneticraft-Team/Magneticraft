package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.ELECTRIC_POLE_PLACE
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.util.get
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
 * Created by cout970 on 05/07/2016.
 */
object BlockElectricPoleAdapter : BlockElectricPoleBase(Material.IRON, "electric_pole_adapter"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if (ELECTRIC_POLE_PLACE[BlockElectricPole.getStateFromMeta(meta)!!].isMainBlock()) {
            return TileElectricPoleAdapter()
        }
        return null
    }

    override fun getDrops(world: IBlockAccess?, pos: BlockPos?, state: IBlockState?, fortune: Int): MutableList<ItemStack>? {
        return super.getDrops(world, pos, state, fortune).apply { add(ItemStack(BlockElectricPole)) }
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return ELECTRIC_POLE_PLACE[state].isMainBlock()
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
    }
}