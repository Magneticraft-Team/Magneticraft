package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.internal.heat.InfiniteHeatContainer
import com.cout970.magneticraft.tileentity.TileBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

/**
 * Created by cout970 on 04/07/2016.
 */
class TileInfiniteHeat(temperature: Double) : TileBase(), ITickable {

    val heat = InfiniteHeatContainer(
            temperature = temperature,
            tile = this)

    override fun update() {
        if (!worldObj.isRemote) {
            heat.updateHeat()
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) {
        heat.refreshConnections()
    }
}