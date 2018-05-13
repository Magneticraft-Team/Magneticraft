package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_CONSUMPTION
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_PRODUCTION
import com.cout970.magneticraft.misc.energy.IMachineEnergyInterface
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.*
import net.minecraft.nbt.NBTTagCompound

class ModuleElectricHeater(
        val energy: IMachineEnergyInterface,
        override val name: String = "module_electric_heater"
) : IModule {

    override lateinit var container: IModuleContainer
    var heat = STANDARD_AMBIENT_TEMPERATURE.toFloat()
    val consumption = ValueAverage()
    val production = ValueAverage()

    companion object {
        @JvmStatic
        val HEAT_RISING_SPEED = 1f
        @JvmStatic
        val HEAT_FALLING_SPEED = 0.25f
        @JvmStatic
        val MAX_HEAT = 200.fromCelsiusToKelvin()
    }

    fun getBoiler(): ModuleSteamBoiler? = world.getModule<ModuleSteamBoiler>(pos.up())

    override fun update() {
        if (world.isClient) return

        val heatToApply = Config.electricHeaterMaxHeatPerTick.toFloat()
        val energyToSpend = heatToApply * ConversionTable.HEAT_TO_FE * ConversionTable.FE_TO_J

        if (!energy.hasEnergy(energyToSpend)) {
            if (heat > STANDARD_AMBIENT_TEMPERATURE) {
                heat -= HEAT_FALLING_SPEED
            }

        } else {
            val boiler = getBoiler()

            if (boiler != null && heat >= WATER_BOILING_POINT) {
                energy.useEnergy(energyToSpend)
                boiler.applyHeat(heatToApply)

                consumption += energyToSpend
                production += heatToApply

            } else if (heat < MAX_HEAT) {
                heat += HEAT_RISING_SPEED
            }
        }

        consumption.tick()
        production.tick()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        heat = nbt.getFloat("heat")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("heat", heat)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
                FloatSyncVariable(DATA_ID_MACHINE_HEAT, { heat }, { heat = it }),
                consumption.toSyncVariable(DATA_ID_MACHINE_CONSUMPTION),
                production.toSyncVariable(DATA_ID_MACHINE_PRODUCTION)
        )
    }
}