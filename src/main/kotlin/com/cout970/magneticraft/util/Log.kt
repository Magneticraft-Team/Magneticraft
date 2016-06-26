package com.cout970.magneticraft.util

import org.apache.logging.log4j.Logger

/**
 * Created by cout970 on 11/06/2016.
 */
object Log {

    lateinit var log: Logger

    fun setLogger(l: Logger) {
        log = l
    }

    fun info(str: String) {
        log.info("[$MODID]$str")
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
        log.info("[$MODID][DEBUG]${s.toString()}")
    }
}