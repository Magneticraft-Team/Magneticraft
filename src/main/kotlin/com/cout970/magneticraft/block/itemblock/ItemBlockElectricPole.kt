package com.cout970.magneticraft.block.itemblock

import com.cout970.magneticraft.block.BlockElectricPole
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 30/06/2016.
 */
class ItemBlockElectricPole : ItemBlockBase(BlockElectricPole) {

    override fun getBlocksToPlace(world: World, pos: BlockPos, facing: EnumFacing, player: EntityPlayer, stack: ItemStack): List<BlockPos> = listOf(pos,
            pos.offset(EnumFacing.UP, 1), pos.offset(EnumFacing.UP, 2), pos.offset(EnumFacing.UP, 3), pos.offset(EnumFacing.UP, 4))
}