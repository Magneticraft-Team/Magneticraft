package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.energy.IMachineEnergyInterface
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_MACHINE_CONSUMPTION
import com.cout970.magneticraft.systems.gui.DATA_ID_MACHINE_PRODUCTION
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer

class ModuleElectricHeater(
    val node: IHeatNode,
    val energy: IMachineEnergyInterface,
    override val name: String = "module_electric_heater"
) : IModule {

    override lateinit var container: IModuleContainer
    val consumption = ValueAverage()
    val production = ValueAverage()

    companion object {
        @JvmStatic
        val MAX_HEAT = 600.fromCelsiusToKelvin()
    }

    override fun update() {
        if (world.isClient) return

        val energy = Config.electricHeaterMaxProduction

        if (!this.energy.hasEnergy(energy)) {
            if (node.temperature > STANDARD_AMBIENT_TEMPERATURE) {
                node.applyHeat(-10.0)
            }

        } else {
            if (node.temperature < MAX_HEAT) {
                this.energy.useEnergy(energy)
                node.applyHeat(energy)

                consumption += energy
                production += energy
            }
        }

        consumption.tick()
        production.tick()
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
            consumption.toSyncVariable(DATA_ID_MACHINE_CONSUMPTION),
            production.toSyncVariable(DATA_ID_MACHINE_PRODUCTION)
        )
    }
}