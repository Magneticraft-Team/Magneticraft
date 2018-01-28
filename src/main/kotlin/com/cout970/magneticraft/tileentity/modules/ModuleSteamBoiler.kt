package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.ConversionTable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 2017/07/13.
 */

class ModuleSteamBoiler(
        val inputTank: Tank,
        val outputTank: Tank,
        val heatCapacity: Float,
        maxProduction: Int,
        override val name: String = "module_steam_boiler"
) : IModule {

    override lateinit var container: IModuleContainer
    val maxWaterPerTick = (maxProduction / ConversionTable.WATER_TO_STEAM).toInt()
    var heatUnits = 0f

    fun applyHeat(heat: Float) {
        if (heat + heatUnits > heatCapacity) return
        heatUnits += heat
    }

    override fun update() {
        if (world.isClient) return
        // has heat
        if (heatUnits <= 0) return
        val waterLimit = inputTank.fluidAmount
        if (waterLimit <= 0) return

        val spaceLimit = (outputTank.capacity - outputTank.fluidAmount) / ConversionTable.WATER_TO_STEAM.toInt()
        if (spaceLimit <= 0) return

        val operations = minOf(minOf(waterLimit, spaceLimit), minOf(maxWaterPerTick, heatUnits.toInt()))
        if (operations <= 0) return

        val fluid = FluidRegistry.getFluid("steam") ?: return
        // boil water
        inputTank.drainInternal(operations, true)
        outputTank.fillInternal(FluidStack(fluid, operations * ConversionTable.WATER_TO_STEAM.toInt()), true)
        heatUnits -= operations
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
                FloatSyncVariable(
                        id = DATA_ID_MACHINE_HEAT,
                        getter = { heatUnits },
                        setter = { heatUnits = it }
                )
        )
    }
}