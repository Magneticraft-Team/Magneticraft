package com.cout970.magneticraft.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * Created by cout970 on 06/12/2015.
 */
public class Log {

    public static Logger LOGGER;

    public static void error(String s) {
        LOGGER.error("[Magneticraft]"+s);
    }

    public static void debug(Object s) {
        LOGGER.info("[Magneticraft][DEBUG]" + s);
    }

    public static void info(String s) {
        LOGGER.info("[Magneticraft]"+s);
    }

    public static void warn(String s) {
        LOGGER.warn("[Magneticraft]"+s);
    }

    public static void raw(String name, Level l, String s) {
        LOGGER.log(l, "[Magneticraft]"+name, s);
    }
}
