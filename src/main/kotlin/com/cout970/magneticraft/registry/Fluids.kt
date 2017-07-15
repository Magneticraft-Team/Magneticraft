package com.cout970.magneticraft.registry

import com.cout970.magneticraft.util.resource
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by cout970 on 2017/07/15.
 */

fun initFluids() {
    if (!FluidRegistry.isFluidRegistered("steam")) {
        FluidRegistry.registerFluid(
                Fluid("steam", resource("fluids/steam_still"), resource("fluids/steam_flow")).also { it.unlocalizedName = "magneticraft.steam" }
        )
    }
}