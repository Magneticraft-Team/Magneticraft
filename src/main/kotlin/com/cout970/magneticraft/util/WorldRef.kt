package com.cout970.magneticraft.util

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 15/05/2016.
 */
data class WorldRef(val world: World, val pos: BlockPos) {

    fun getBlockState(): IBlockState = world.getBlockState(pos)

    fun setBlockState(state: IBlockState) = world.setBlockState(pos, state)

    fun setBlockState(state: IBlockState, num: Int) = world.setBlockState(pos, state, num)
}