@file:Suppress("unused")

package com.cout970.magneticraft.misc

/**
 * Created by cout970 on 2017/07/15.
 */
object ConversionTable {

    // Energy units
    const val FE_TO_RF = 1.0          // 1FE = 1RF;    | 1RF = 1FE
    const val FE_TO_TESLA = 1.0       // 1FE = 1T;     | 1T  = 1FE
    const val FE_TO_J = 1.0           // 1FE = 1J;     | 1J  = 1FE
    const val FE_TO_EU = 0.25         // 1FE = 0.25EU; | 1EU = 4FE
    const val MJ_TO_FE = 10           // 1FE = 0.1MJ;  | 1MJ = 10FE

    // Steam conversion
    const val STEAM_TO_FE = 2.0       // 1mB = 2.0FE   | 1FE = 0.5mb (20mb = 4MJ = 40RF = 40FE; using 2 MJ = 5 EU ratio)
    const val STEAM_TO_EU = 0.5       // 1mB = 0.5EU   | 1EU = 2mb (STEAM_TO_FE * FE_TO_EU)
    const val STEAM_TO_J = 2.0        // 1mB = 2.0J;   | 1J  = 0.5mB

    // Fuel (minecraft furnace ticks per item)
    const val FUEL_TO_FE = 10.0       // 1fuel = 10FE  | 1FE = 0.1fuel
    const val FUEL_TO_J = 10.0        // 1fuel = 10J  | 1J = 0.1fuel

    // Fluids
    const val WATER_TO_STEAM = 10.0   // 1mB Water = 10mB Steam | 1mB Steam = 0.1 mB Water

    // Fuel conversion (Fuel per tick)
    const val FT_TO_FE = 10.0         // 1FT = 10FE    | 1FE = 0.1FT

    // Why not?
    const val AVOGADRO_CONSTANT = 6.022E23

    // Pressure
    const val BAR_TO_PA = 100000.0
    const val PSI_TO_PA = 6894.75729
}