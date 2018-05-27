package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.ConversionTable

/**
 * Created by cout970 on 2017/07/18.
 */
class ModuleSteamGenerator(
        val steamTank: Tank,
        val storage: ModuleInternalStorage,
        override val name: String = "module_steam_generator"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        const val MAX_ENERGY_PER_TICK = 250
        const val STEAM_PER_OPERATION = 10
        const val ENERGY_PER_OPERATION = (STEAM_PER_OPERATION * ConversionTable.STEAM_TO_J).toInt()
        const val MAX_OPERATIONS_PER_TICK = MAX_ENERGY_PER_TICK / ENERGY_PER_OPERATION
    }

    val production = ValueAverage()

    fun getAvailableOperations(): Int {
        if (steamTank.fluidAmount < STEAM_PER_OPERATION) return 0

        val fluidLimit = steamTank.fluidAmount / STEAM_PER_OPERATION
        val energyLimit = (storage.capacity - storage.energy) / ENERGY_PER_OPERATION
        return Math.min(Math.min(fluidLimit, energyLimit), MAX_OPERATIONS_PER_TICK)
    }

    override fun update() {
        if (world.isClient) return
        val operations = getAvailableOperations()
        if (operations > 0) {
            steamTank.drain(STEAM_PER_OPERATION * operations, true)
            storage.energy += ENERGY_PER_OPERATION * operations
            production += operations
        }
        production.tick()
    }
}