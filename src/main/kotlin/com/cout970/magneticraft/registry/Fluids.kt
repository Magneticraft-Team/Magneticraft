package com.cout970.magneticraft.registry

import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.WATER_BOILING_POINT
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.misc.resource
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

    if (!FluidRegistry.isFluidRegistered("hot_crude")) {
        FluidRegistry.registerFluid(
            Fluid("hot_crude", resource("fluids/hot_crude_still"), resource("fluids/hot_crude_flow")).also {
                it.unlocalizedName = "magneticraft.hot_crude"
                it.temperature = 600.fromCelsiusToKelvin().toInt()
                it.density = 10
                it.isGaseous = true
                it.viscosity = 20
            }
        )
    }

    // first stage oil processing
    if (!FluidRegistry.isFluidRegistered("lpg")) {
        FluidRegistry.registerFluid(
            Fluid("lpg", resource("fluids/lpg_still"), resource("fluids/lpg_flow")).also {
                it.unlocalizedName = "magneticraft.lpg"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 300
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("light_oil")) {
        FluidRegistry.registerFluid(
            Fluid("light_oil", resource("fluids/light_oil_still"), resource("fluids/light_oil_flow")).also {
                it.unlocalizedName = "magneticraft.light_oil"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 700
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("heavy_oil")) {
        FluidRegistry.registerFluid(
            Fluid("heavy_oil", resource("fluids/heavy_oil_still"), resource("fluids/heavy_oil_flow")).also {
                it.unlocalizedName = "magneticraft.heavy_oil"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 800
                it.viscosity = 1000
            }
        )
    }

    // second state (lpg)
    if (!FluidRegistry.isFluidRegistered("natural_gas")) {
        FluidRegistry.registerFluid(
            Fluid("natural_gas", resource("fluids/natural_gas_still"), resource("fluids/natural_gas_flow")).also {
                it.unlocalizedName = "magneticraft.natural_gas"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 1
                it.viscosity = 10
                it.isGaseous = true
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("naphtha")) {
        FluidRegistry.registerFluid(
            Fluid("naphtha", resource("fluids/naphtha_still"), resource("fluids/naphtha_flow")).also {
                it.unlocalizedName = "magneticraft.naphtha"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 800
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("plastic")) {
        FluidRegistry.registerFluid(
            Fluid("plastic", resource("fluids/plastic_still"), resource("fluids/plastic_flow")).also {
                it.unlocalizedName = "magneticraft.plastic"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 900
                it.viscosity = 1000
            }
        )
    }

    // second state (light_oil)
    if (!FluidRegistry.isFluidRegistered("gasoline")) {
        FluidRegistry.registerFluid(
            Fluid("gasoline", resource("fluids/gasoline_still"), resource("fluids/gasoline_flow")).also {
                it.unlocalizedName = "magneticraft.gasoline"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 500
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("kerosene")) {
        FluidRegistry.registerFluid(
            Fluid("kerosene", resource("fluids/kerosene_still"), resource("fluids/kerosene_flow")).also {
                it.unlocalizedName = "magneticraft.kerosene"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 600
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("diesel")) {
        FluidRegistry.registerFluid(
            Fluid("diesel", resource("fluids/diesel_still"), resource("fluids/diesel_flow")).also {
                it.unlocalizedName = "magneticraft.diesel"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 700
                it.viscosity = 1000
            }
        )
    }

    // second state (heavy_oil)
    if (!FluidRegistry.isFluidRegistered("lubricant")) {
        FluidRegistry.registerFluid(
            Fluid("lubricant", resource("fluids/lubricant_still"), resource("fluids/lubricant_flow")).also {
                it.unlocalizedName = "magneticraft.lubricant"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 600
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("fuel")) {
        FluidRegistry.registerFluid(
            Fluid("fuel", resource("fluids/fuel_still"), resource("fluids/fuel_flow")).also {
                it.unlocalizedName = "magneticraft.fuel"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 700
                it.viscosity = 1000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("oil_residue")) {
        FluidRegistry.registerFluid(
            Fluid("oil_residue", resource("fluids/oil_residue_still"), resource("fluids/oil_residue_flow")).also {
                it.unlocalizedName = "magneticraft.oil_residue"
                it.temperature = STANDARD_AMBIENT_TEMPERATURE.toInt()
                it.density = 800
                it.viscosity = 2000
            }
        )
    }

    if (!FluidRegistry.isFluidRegistered("wood_gas")) {
        FluidRegistry.registerFluid(
            Fluid("wood_gas", resource("fluids/wood_gas_still"), resource("fluids/wood_gas_flow")).also {
                it.unlocalizedName = "magneticraft.wood_gas"
                it.temperature = WATER_BOILING_POINT.toInt()
                it.density = 1
                it.viscosity = 10
                it.isGaseous = true
            }
        )
    }

    val names = listOf("steam", "oil", "hot_crude", "lpg", "light_oil", "heavy_oil", "natural_gas", "naphtha",
        "plastic", "gasoline", "kerosene", "diesel", "lubricant", "fuel", "oil_residue", "wood_gas")

    names.forEach {
        val fluid = FluidRegistry.getFluid(it)
        FluidRegistry.addBucketForFluid(fluid)
    }
}