package com.cout970.magneticraft.block.heat


import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.tileentity.heat.TileIcebox
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
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockIcebox : BlockHeatMultistate(Material.ROCK, "icebox") {

    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileIcebox()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val item = playerIn.getHeldItem(hand)
        if (item != null) {
            if (item.hasCapability(FLUID_HANDLER, null)) {
                val cap = item.getCapability(FLUID_HANDLER, null)
                val tile = worldIn.getTile<TileIcebox>(pos)
                if (tile != null) {
                    val filled = cap.fill(tile.tank.fluid, false)
                    if (filled == 1000) {
                        val accepted = tile.tank.drain(filled, true)
                        if (accepted != null) {
                            cap.fill(accepted, true)
                        }
                        return true
                    }
                    val drained = cap.drain(1000, false)
                    if (drained != null) {
                        val accepted = tile.tank.fill(drained, true)
                        if (accepted > 0 && !playerIn.capabilities.isCreativeMode) {
                            cap.drain(FluidStack(drained, accepted), true)
                        }
                        return true
                    }
                }
            }
        }
        if (!playerIn.isSneaking) {
            if (worldIn.isServer) {
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