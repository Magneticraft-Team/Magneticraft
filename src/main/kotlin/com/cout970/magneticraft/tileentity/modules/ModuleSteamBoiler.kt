package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer

/**
 * Created by cout970 on 2017/07/13.
 */

class ModuleSteamBoiler(
        val inputTank: Tank,
        val outputTank: Tank,
        override val name: String = "module_steam_boiler"
) : IModule {

    override lateinit var container: IModuleContainer
    val MAX_HEAT_UNITS = 100f
    var heatUnits = 0f

    fun applyHeat(heat: Float) {
        if (heat + heatUnits > MAX_HEAT_UNITS) return
        heatUnits += heat
    }

    override fun update() {
        if (world.isServer) {
//            if (heatUnits > 0) {
//                heatUnits--
//                inputTank.drain(1, true)
//                outputTank.fill(FluidStack(FluidRegistry.getFluid("steam"), 10), true)
//            }
        }
    }
}