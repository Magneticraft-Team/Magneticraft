package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.MagneticraftApi
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.liquid.ILiquidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.FluidFuel")
@ZenRegister
object FluidFuel {

    @Suppress("DEPRECATION")
    @ZenMethod
    @JvmStatic
    fun addFuel(liquid: ILiquidStack, burningTime: Int, powerPerCycle: Double) {
        CraftTweakerPlugin.delayExecution {
            val fluidStack = liquid.toStack()

            if (fluidStack == null) {
                ctLogError("[FluidFuel] Invalid input liquid: $liquid")
                return@delayExecution
            }

            if (burningTime <= 0) {
                ctLogError("[FluidFuel] Invalid burning time: $burningTime for liquid $liquid")
                return@delayExecution
            }

            if (powerPerCycle <= 0) {
                ctLogError("[FluidFuel] Invalid power per cycle: $powerPerCycle for liquid $liquid")
                return@delayExecution
            }

            val manager = MagneticraftApi.getFluidFuelManager()
            val fuel = manager.createFuel(fluidStack, burningTime, powerPerCycle)
            manager.registerFuel(fuel)
        }
    }

    @Suppress("DEPRECATION")
    @ZenMethod
    @JvmStatic
    fun removeFuel(liquid: ILiquidStack) {
        CraftTweakerPlugin.delayExecution {
            val fluidStack = liquid.toStack()

            if (fluidStack == null) {
                ctLogError("[FluidFuel] Invalid input liquid: $liquid")
                return@delayExecution
            }

            val manager = MagneticraftApi.getFluidFuelManager()
            val fuel = manager.findFuel(fluidStack)
            if (fuel == null) {
                ctLogError("[FluidFuel] Cannot remove recipe: No recipe found for $liquid")
                return@delayExecution
            }

            applyAction("Removing $fuel") {
                manager.removeFuel(fuel)
            }
        }
    }
}