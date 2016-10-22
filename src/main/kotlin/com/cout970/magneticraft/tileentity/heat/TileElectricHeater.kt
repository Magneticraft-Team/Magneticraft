package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.registry.HEAT_HANDLER
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import com.cout970.magneticraft.util.TIER_1_MACHINES_MIN_VOLTAGE
import com.cout970.magneticraft.util.misc.IBD
import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by cout970 on 04/07/2016.
 */

class TileElectricHeater(
) : TileElectricBase(), ITickable {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)
    override val electricNodes: List<IElectricNode>
        get() = listOf(mainNode)

    companion object {
        val FUEL_TO_HEAT = 0.5f
        val SPEED = 1
        val DEFAULT_MAX_TEMP = 400.toKelvinFromCelsius()
    }

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT).toLong(),
            conductivity = 0.05,
            tile = this)

    override fun update() {
        if (!worldObj.isRemote) {
            if (mainNode.voltage >= TIER_1_MACHINES_MIN_VOLTAGE && heat.heat < heat.maxHeat) {
                val applied = mainNode.applyPower(-Config.electricHeaterMaxConsumption * interpolate(mainNode.voltage, 60.0, 70.0), false)
                val fuel = SPEED * applied.toFloat() / Config.electricHeaterMaxConsumption.toFloat()
                heat.pushHeat((fuel * FUEL_TO_HEAT).toLong(), false)
            }
            heat.updateHeat()
        }
        super.update()
    }

    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getLong(DATA_ID_MACHINE_WORKING, { heat.heat = it })
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setLong("heat", heat.heat)
    }

    override fun load(nbt: NBTTagCompound) {
        heat.heat = nbt.getLong("heat")
        heat.refreshConnections()
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