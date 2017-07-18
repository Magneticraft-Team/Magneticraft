package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.ConversionTable
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 2017/07/13.
 */

class ModuleSteamBoiler(
        val inputTank: Tank,
        val outputTank: Tank,
        override val name: String = "module_steam_boiler"
) : IModule {

    override lateinit var container: IModuleContainer
    var heatUnits = 0f

    companion object {
        val MAX_HEAT_UNITS = 100f
        val MAX_STEAM_PER_TICK = 40
        val MAX_WATER_PER_TICK = (MAX_STEAM_PER_TICK / ConversionTable.WATER_TO_STEAM).toInt()
    }

    fun applyHeat(heat: Float) {
        if (heat + heatUnits > MAX_HEAT_UNITS) return
        heatUnits += heat
    }

    override fun update() {
        if (world.isClient) return
        fillTopTank()
        // has heat
        if (heatUnits <= 0) return
        val waterLimit = inputTank.fluidAmount
        if (waterLimit <= 0) return

        val spaceLimit = (outputTank.capacity - outputTank.fluidAmount) / ConversionTable.WATER_TO_STEAM.toInt()
        if (spaceLimit <= 0) return

        val operations = listOf(waterLimit, spaceLimit, MAX_WATER_PER_TICK, heatUnits.toInt()).min()!!
        if (operations <= 0) return

        val fluid = FluidRegistry.getFluid("steam") ?: return
        // boil water
        inputTank.drainInternal(operations, true)
        outputTank.fillInternal(FluidStack(fluid, operations * ConversionTable.WATER_TO_STEAM.toInt()), true)
        heatUnits -= operations
    }

    fun fillTopTank() {
        val stack = outputTank.fluid ?: return
        if (stack.amount <= 0) return
        val tile = world.getTileEntity(pos.up()) ?: return
        val handler = tile.getOrNull(FLUID_HANDLER, EnumFacing.DOWN) ?: return

        val amount = handler.fill(stack, false)
        if (amount <= 0) return
        handler.fill(FluidStack(stack, amount), true)
        outputTank.drainInternal(amount, true)
    }
}