package com.cout970.magneticraft.block



import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.util.set
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.computer.TileComputer
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.isServer
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
 * Created by cout970 on 2016/09/30.
 */
object BlockComputer : BlockMultiState(Material.IRON, "computer"), ITileEntityProvider {

    init {
        lightOpacity = 0
    }

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileComputer()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {

            if (worldIn.isServer) {
                var block = true
                if (heldItem != null) {
                    val cap = ITEM_FLOPPY_DISK!!.fromItem(heldItem)
                    if (cap != null) {
                        val tile = worldIn.getTile<TileComputer>(pos)
                        if (tile != null && tile.inv[0] == null) {
                            val index = playerIn.inventory.currentItem
                            if (index >= 0 && index < 9) {
                                tile.inv[0] = playerIn.inventory.removeStackFromSlot(index)
                            }
                        } else {
                            block = false
                        }
                    } else {
                        block = false
                    }
                } else {
                    block = false
                }

                if (!block) {
                    playerIn.openGui(Magneticraft, -1, worldIn, pos.x, pos.y, pos.z)
                }
            }
            return true
        } else {
            if (worldIn.isServer && hand == EnumHand.MAIN_HAND && heldItem == null) {
                val tile = worldIn.getTile<TileComputer>(pos)
                if (tile != null && tile.inv[0] != null) {
                    playerIn.inventory.addItemStackToInventory(tile.inv.extractItem(0, 64, false))
                }
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        worldIn?.setBlockState(pos, defaultState.withProperty(PROPERTY_DIRECTION, placer.horizontalFacing.opposite))
    }

    override fun getMetaFromState(state: IBlockState): Int = PROPERTY_DIRECTION[state].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta))

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_DIRECTION)
}