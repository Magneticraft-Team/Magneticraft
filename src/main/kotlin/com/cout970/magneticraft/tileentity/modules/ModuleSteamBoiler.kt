package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
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
        if (world.isClient) return
        fillTopTank()
        // has heat
        if (heatUnits <= 0) return
        // has water
        inputTank.drainInternal(1, false) ?: return
        // steam FluidStack
        val fluid = FluidRegistry.getFluid("steam") ?: return
        val fluidStack = FluidStack(fluid, ConversionTable.WATER_TO_STEAM.toInt())
        //has space for steam
        if (outputTank.fillInternal(fluidStack, false) != fluidStack.amount) return

        // boil water
        inputTank.drainInternal(1, true)
        outputTank.fillInternal(fluidStack, true)
        heatUnits--
    }

    fun fillTopTank() {
        val stack = outputTank.fluid ?: return
        if (stack.amount <= 0) return
        val tile = world.getTileEntity(pos.up()) ?: return
        val handler = tile.getOrNull(FLUID_HANDLER) ?: return

        val amount = handler.fill(stack, false)
        if (amount <= 0) return
        handler.fill(FluidStack(stack, amount), true)
        outputTank.drainInternal(amount, true)
    }
}