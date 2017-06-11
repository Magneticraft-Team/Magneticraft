package com.cout970.magneticraft.block.fuel

import com.cout970.magneticraft.item.ItemCoke
import com.cout970.magneticraft.misc.fuel.IFuel
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object BlockCoke : BlockMod("block_coal_coke", Material.ROCK), IFuel<Block> {

    override fun getBurnTime(): Int = ItemCoke.getBurnTime() * 10
    override fun getFlammability(access: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = 5
    override fun getFireSpreadSpeed(access: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = 5
}