package com.cout970.magneticraft.block.itemblock

import com.cout970.magneticraft.block.BlockIncendiaryGenerator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 17/07/2016.
 */
class ItemBlockIncendiaryGenerator  : ItemBlockBase(BlockIncendiaryGenerator) {

    override fun getBlocksToPlace(world: World, pos: BlockPos, facing: EnumFacing, player: EntityPlayer, stack: ItemStack): List<BlockPos>  =
            listOf(pos, pos.up())
}