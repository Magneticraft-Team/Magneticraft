package com.cout970.magneticraft.multiblock

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 21/08/2016.
 */
interface ITileMultiblock {

    //current multiblock
    var multiblock: Multiblock?
    //relative position from the multiblock center to this block
    var centerPos: BlockPos?
    //orientation of the multiblock
    var multiblockFacing: EnumFacing?

    fun onActivate() = Unit

    fun onDeactivate() = Unit
}