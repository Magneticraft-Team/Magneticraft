package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockBase
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.IFuel
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object BlockFiberboard : BlockBase(Material.WOOD, "fiberboard_block"), IFuel<Block> {
    override fun getBurnTime(): Int = Config.fiberboardBurnTime
    override fun getFlammability(access: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = 5
    override fun getFireSpreadSpeed(access: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = 10
}