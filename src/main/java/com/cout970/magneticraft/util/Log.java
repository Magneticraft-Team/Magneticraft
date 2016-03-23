package com.cout970.magneticraft.util;

import net.darkaqua.blacksmith.log.ILog;
import net.darkaqua.blacksmith.log.LogFactory;

/**
 * Created by cout970 on 06/12/2015.
 */
public class Log {

    private static ILog LOGGER;

    static {
        LOGGER = LogFactory.createLog("Magneticraft");
    }

    public static void error(String s) {
        LOGGER.error(s);
    }

    public static void debug(Object s) {
        LOGGER.info(String.valueOf(s));
    }

    public static void info(String s) {
        LOGGER.info(s);
    }

    public static void warn(String s) {
        LOGGER.warn(s);
    }
}
