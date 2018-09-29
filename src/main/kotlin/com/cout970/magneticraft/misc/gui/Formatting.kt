package com.cout970.magneticraft.misc.gui

import com.cout970.magneticraft.misc.toCelsius
import com.cout970.magneticraft.misc.toFahrenheit
import com.cout970.magneticraft.systems.config.Config

/**
 * Created by cout970 on 2017/07/13.
 */

fun formatHeat(amount: Double): String {
    val value = if (Config.heatUnitCelsius) amount.toCelsius() else amount.toFahrenheit()
    val symbol = if (Config.heatUnitCelsius) "C" else "F"
    return "%.1f%s".format(value, symbol)
}