package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.isIn
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 25/08/2016.
 */
class TileInserter : TileBase() {

    fun getDirection(): EnumFacing {
        val state = getBlockState()
        if (PROPERTY_DIRECTION.isIn(state)) {
            return PROPERTY_DIRECTION[state]
        }
        return EnumFacing.NORTH
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}