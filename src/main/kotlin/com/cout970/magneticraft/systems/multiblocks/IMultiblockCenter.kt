package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.AABB
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 22/08/2016.
 */
interface IMultiblockCenter : IMultiblockModule {

    fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean

    fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T?

    fun getDynamicCollisionBoxes(otherPos: BlockPos): List<AABB>
}