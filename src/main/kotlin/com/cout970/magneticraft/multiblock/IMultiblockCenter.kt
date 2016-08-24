package com.cout970.magneticraft.multiblock

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 22/08/2016.
 */
interface IMultiblockCenter : ITileMultiblock {

    fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean

    fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T?
}