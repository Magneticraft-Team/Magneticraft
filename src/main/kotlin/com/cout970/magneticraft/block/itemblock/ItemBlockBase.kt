package com.cout970.magneticraft.block.itemblock

import com.cout970.magneticraft.block.BlockBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ItemBlockBase(
    val blockBase: BlockBase
) : ItemBlock(blockBase) {
    init {
        registryName = block.registryName
        unlocalizedName = block.unlocalizedName
    }

    override fun getHasSubtypes() = blockBase.inventoryVariants.size > 1

    override fun getUnlocalizedName(stack: ItemStack?) = blockBase.getItemName(stack) ?: "unnamed"

    override fun getMetadata(damage: Int) = damage

    /**
     * This method is override to avoid removing blocks when the block cannot be placed, this
     * happens because this method only checks the center block and not the other blocks
     */

    override fun onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos_: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        var pos = pos_
        val blockState = worldIn.getBlockState(pos)
        val block = blockState.block

        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(facing)
        }

        if (stack.stackSize != 0 && canPlace(worldIn, pos, facing, playerIn, stack)) {
            val i = this.getMetadata(stack.metadata)
            val placedBlockState = this.block.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn)

            if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, placedBlockState)) {
                val soundType = this.block.soundType
                worldIn.playSound(playerIn, pos, soundType.placeSound, SoundCategory.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f)
                --stack.stackSize
            }

            return EnumActionResult.SUCCESS
        } else {
            return EnumActionResult.FAIL
        }
    }

    private fun canPlace(world: World, pos: BlockPos, facing: EnumFacing, player: EntityPlayer, stack: ItemStack): Boolean {
        return getBlocksToPlace(world, pos, facing, player, stack).all { checkCanPlace(world, it, facing, player, stack) }
    }

    open fun getBlocksToPlace(world: World, pos: BlockPos, facing: EnumFacing, player: EntityPlayer, stack: ItemStack): List<BlockPos> = listOf(pos)

    fun checkCanPlace(world: World, pos: BlockPos, facing: EnumFacing, player: EntityPlayer, stack: ItemStack): Boolean {
        return player.canPlayerEdit(pos, facing, stack) && world.canBlockBePlaced(this.block, pos, false, facing, null, stack)
    }
}
