package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.EntityPlayer
import com.cout970.magneticraft.EnumFacing

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 20/08/2016.
 */
data class MultiblockContext(
    val multiblock: Multiblock,
    val world: World,
    val center: BlockPos,
    val facing: EnumFacing,
    val player: EntityPlayer?
)