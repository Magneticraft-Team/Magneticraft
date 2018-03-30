package com.cout970.magneticraft.registry

import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.WATER_BOILING_POINT
import com.cout970.magneticraft.util.resource
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by cout970 on 2017/07/15.
 */

fun initFluids() {

//  blockstates/oil_source.json has the texture locations so they get loaded with the model

    if (!FluidRegistry.isFluidRegistered("steam")) {
        FluidRegistry.registerFluid(
                Fluid("steam", resource("fluids/steam_still"), resource("fluids/steam_flow")).also {
                    it.unlocalizedName = "magneticraft.steam"
                    it.temperature = WATER_BOILING_POINT.toInt()
                    it.density = 1
                    it.viscosity = 10
                    it.isGaseous = true
                }
        )
    }

    if (!FluidRegistry.isFluidRegistered("oil")) {
        FluidRegistry.registerFluid(
                Fluid("oil", resource("fluids/oil_still"), resource("fluids/oil_flow")).also {
                    it.unlocalizedName = "magneticraft.oil"
                    it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                    it.density = 1100
                    it.viscosity = 2000
                }
        )
    }
}