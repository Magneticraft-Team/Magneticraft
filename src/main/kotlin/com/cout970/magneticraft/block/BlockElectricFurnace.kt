package com.cout970.magneticraft.block

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.electric.TileElectricFurnace
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockElectricFurnace : BlockMultiState(Material.IRON, "electric_furnace"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileElectricFurnace()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if(worldIn.isServer) {
                playerIn.openGui(Magneticraft, -1, worldIn, pos.x, pos.y, pos.z)
            }
            return true
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        worldIn?.setBlockState(pos, defaultState.withProperty(PROPERTY_DIRECTION, placer.horizontalFacing.opposite))
    }

    override fun getMetaFromState(state: IBlockState): Int = state[PROPERTY_DIRECTION].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta))

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_DIRECTION)
}