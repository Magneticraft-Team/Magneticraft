@file:Suppress("unused")

package com.cout970.magneticraft.util

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft

/**
 * Created by cout970 on 29/06/2016.
 */

fun logError(str: String, vararg args: Any) {
    Magneticraft.log.error("[$MOD_ID]$str", *args)
}

fun info(str: String, vararg args: Any) {
    Magneticraft.log.info("[$MOD_ID]$str", *args)
}

fun debug(vararg obj: Any?) {
    Magneticraft.log.info("[$MOD_ID][DEBUG]${obj.joinToString()}")
}