package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.tempToEnergy
import com.cout970.magneticraft.misc.ConversionTable
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.gui.DATA_ID_MACHINE_PRODUCTION
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 2017/07/13.
 */

class ModuleSteamBoiler(
    val node: IHeatNode,
    val inputTank: Tank,
    val outputTank: Tank,
    val maxProduction: Int,
    override val name: String = "module_steam_boiler"
) : IModule {

    override lateinit var container: IModuleContainer
    val maxWaterPerTick = (maxProduction / ConversionTable.WATER_TO_STEAM).toInt()
    val production = ValueAverage()

    override fun update() {
        if (world.isClient) return

        production.tick()

        val waterLimit = inputTank.fluidAmount
        if (waterLimit <= 0) return

        val spaceLimit = (outputTank.capacity - outputTank.fluidAmount) / ConversionTable.WATER_TO_STEAM.toInt()
        if (spaceLimit <= 0) return

        if (node.temperature < 100.fromCelsiusToKelvin()) return

        val heatEnergy = tempToEnergy(node, node.temperature - 100.fromCelsiusToKelvin())
        val heatLimit = (heatEnergy / ConversionTable.STEAM_TO_J / ConversionTable.WATER_TO_STEAM).toInt()

        val water = minOf(minOf(waterLimit, spaceLimit), minOf(maxWaterPerTick, heatLimit))
        if (water <= 0) return

        val fluid = FluidRegistry.getFluid("steam") ?: return
        val steam = (water * ConversionTable.WATER_TO_STEAM).toInt()

        // boil water
        inputTank.drainInternal(water, true)
        outputTank.fillInternal(FluidStack(fluid, steam), true)
        production += steam
        node.applyHeat(-steam * ConversionTable.STEAM_TO_J)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(production.toSyncVariable(DATA_ID_MACHINE_PRODUCTION))
    }
}