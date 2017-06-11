@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.itemblock.ItemBlockElectricPoleAdapter
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBlock
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
        if (BlockElectricPole.getStateFromMeta(meta)!![ELECTRIC_POLE_PLACE].isMainBlock()) {
            return TileElectricPoleAdapter()
        }
        return null
    }

    override fun createItemForm(): ItemBlock? {
        return ItemBlockElectricPoleAdapter(this)
    }

    override fun getDrops(world: IBlockAccess?, pos: BlockPos?, state: IBlockState?, fortune: Int): MutableList<ItemStack>? {
        return super.getDrops(world, pos, state, fortune).apply { add(ItemStack(BlockElectricPole)) }
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return state[ELECTRIC_POLE_PLACE].isMainBlock()
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
    }
}