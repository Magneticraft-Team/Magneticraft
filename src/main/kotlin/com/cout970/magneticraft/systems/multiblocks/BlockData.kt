package com.cout970.magneticraft.systems.multiblocks

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 20/08/2016.
 */
data class BlockData(
    val state: IBlockState,
    val pos: BlockPos
)