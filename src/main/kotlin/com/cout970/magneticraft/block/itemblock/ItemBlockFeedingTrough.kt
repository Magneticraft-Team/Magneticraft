package com.cout970.magneticraft.block.itemblock

import com.cout970.magneticraft.block.BlockFeedingTrough
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 24/06/2016.
 */
class ItemBlockFeedingTrough() : ItemBlockBase(BlockFeedingTrough) {

    override fun getBlocksToPlace(world: World, pos: BlockPos, facing: EnumFacing, player: EntityPlayer, stack: ItemStack): List<BlockPos> =
            listOf(pos, pos.add(player.adjustedHorizontalFacing.directionVec))
}