package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockFallingBase
import com.cout970.magneticraft.item.ItemWoodChip
import com.cout970.magneticraft.misc.fuel.IFuel
import net.minecraft.block.Block
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object BlockWoodChip : BlockFallingBase("block_wood_chip"), IFuel<Block> {
    override fun getBurnTime(): Int = ItemWoodChip.getBurnTime() * 10
    override fun getFlammability(access: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = 10
    override fun getFireSpreadSpeed(access: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = 25
}