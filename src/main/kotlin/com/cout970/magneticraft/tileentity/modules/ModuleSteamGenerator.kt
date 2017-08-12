package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
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
        val MAX_ENERGY_PER_TICK = 160
        val STEAM_PER_OPERATION = 10
        val ENERGY_PER_OPERATION = (STEAM_PER_OPERATION * ConversionTable.STEAM_TO_J).toInt()
        val MAX_OPERATIONS_PER_TICK = MAX_ENERGY_PER_TICK / ENERGY_PER_OPERATION
    }

    override fun update() {
        if (world.isClient) return
        if (steamTank.fluidAmount < STEAM_PER_OPERATION) return

        val fluidLimit = steamTank.fluidAmount / STEAM_PER_OPERATION
        val energyLimit = (storage.capacity - storage.energy) / ENERGY_PER_OPERATION
        val operations = Math.min(Math.min(fluidLimit, energyLimit), MAX_OPERATIONS_PER_TICK)

        if (operations > 0) {
            steamTank.drain(STEAM_PER_OPERATION * operations, true)
            storage.energy += ENERGY_PER_OPERATION * operations
        }
    }
}