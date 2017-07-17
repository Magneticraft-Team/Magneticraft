package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.ConversionTable

/**
 * Created by cout970 on 2017/07/18.
 */
class ModuleSteamEngine(
        val steamTank: Tank,
        val storage: ModuleInternalStorage,
        override val name: String = "module_steam_engine"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        val STEAM_PER_TICK = 10
        val ENERGY_PER_TICK = (STEAM_PER_TICK * ConversionTable.STEAM_TO_J).toInt()
    }

    override fun update() {
        if (world.isClient) return
        if (steamTank.fluidAmount > STEAM_PER_TICK && (storage.capacity - storage.energy) > ENERGY_PER_TICK) {
            steamTank.drain(STEAM_PER_TICK, true)
            storage.energy += ENERGY_PER_TICK
        }
    }
}