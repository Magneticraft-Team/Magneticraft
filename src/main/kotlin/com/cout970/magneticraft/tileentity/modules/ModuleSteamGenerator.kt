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
        val MAX_ENERGY_PER_TICK = 160
        val STEAM_PER_OPERATION = 10
        val ENERGY_PER_OPERATION = (STEAM_PER_OPERATION * ConversionTable.STEAM_TO_J).toInt()
        val MAX_OPERATIONS_PER_TICK = MAX_ENERGY_PER_TICK / ENERGY_PER_OPERATION
    }

    val producction = ValueAverage()

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
            producction += operations
        }
        producction.tick()
    }
}