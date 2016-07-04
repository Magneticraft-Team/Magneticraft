package com.cout970.magneticraft.util

import com.cout970.magneticraft.Magneticraft

/**
 * Created by cout970 on 29/06/2016.
 */

fun error(str: String, vararg args: Any) {
    Magneticraft.log.error("[$MODID]$str", *args)
}

fun info(str: String, vararg args: Any) {
    Magneticraft.log.info("[$MODID]$str", *args)
}

fun debug(vararg obj: Any?) {
    val s = StringBuilder()
    var i = obj.size
    for (o in obj) {
        s.append(o)
        i--
        if (i > 0) {
            s.append(", ")
        }
    }
    Magneticraft.log.info("[$MODID][DEBUG]${s.toString()}")
}