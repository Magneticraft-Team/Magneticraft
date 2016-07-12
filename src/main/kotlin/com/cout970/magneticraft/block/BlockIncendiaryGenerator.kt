package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockIncendiaryGenerator : BlockBase(Material.IRON, "incendiary_generator"), ITileEntityProvider {

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileIncendiaryGenerator()

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(worldIn!!.isRemote)return true
        pos!!
        playerIn!!
        val item = playerIn.getHeldItem(hand)
        if (item != null) {
            if (item.hasCapability(FLUID_HANDLER, null)) {
                val cap = item.getCapability(FLUID_HANDLER, null)
                val tile = worldIn.getTile<TileIncendiaryGenerator>(pos)
                if (tile != null) {
                    val drained = cap.drain(FluidStack(FluidRegistry.WATER, 1000), false)
                    val accepted = tile.tank.fill(drained, true)
                    if (accepted > 0 && !playerIn.capabilities.isCreativeMode) {
                        cap.drain(FluidStack(FluidRegistry.WATER, accepted), true)
                    }
                    return true
                }
            }
        }
        playerIn.openGui(Magneticraft, -1, worldIn, pos.x, pos.y, pos.z)
        return true
    }
}