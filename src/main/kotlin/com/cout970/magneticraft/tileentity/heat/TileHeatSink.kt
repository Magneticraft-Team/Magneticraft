package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.registry.HEAT_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 04/07/2016.
 */
class TileHeatSink : TileBase(), ITickable {

    val heat = HeatContainer(dissipation = 0.05,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT).toLong(),
            conductivity = 0.05,
            tile = this)

    override fun update() {
        if (!worldObj.isRemote) {
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setLong("heat", heat.heat)
    }

    override fun load(nbt: NBTTagCompound) {
        heat.heat = nbt.getLong("heat")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == HEAT_HANDLER) return heat as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == HEAT_HANDLER) return true
        return super.hasCapability(capability, facing)
    }
}