package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.states.PROPERTY_DIRECTION
import com.cout970.magneticraft.tileentity.electric.TileElectricBase
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.isIn
import com.cout970.magneticraft.util.misc.AnimationTimer
import com.cout970.magneticraft.util.plus
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 19/08/2016.
 */
class TileHydraulicPress : TileElectricBase() {

    val direction: EnumFacing get() = if(PROPERTY_DIRECTION.isIn(getBlockState()))
        PROPERTY_DIRECTION[getBlockState()] else EnumFacing.NORTH
    val active: Boolean get() = if(BlockHydraulicPress.PROPERTY_ACTIVE.isIn(getBlockState()))
        BlockHydraulicPress.PROPERTY_ACTIVE[getBlockState()] else false

    val hammerAnimation = AnimationTimer()

    override val electricNodes: List<IElectricNode> get() = listOf()

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(1, 0, 1)) to (pos + BlockPos(2, 4, 2))
}