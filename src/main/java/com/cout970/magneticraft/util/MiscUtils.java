package com.cout970.magneticraft.util;

import net.darkaqua.blacksmith.api.util.Direction;

/**
 * Created by cout970 on 15/01/2016.
 */
public class MiscUtils {

    public static void rotate(Direction dir){
//        Log.debug(dir);
        switch (dir){
            //TODO
        }
    }

    public static String capitalice(String name) {
        char a = name.charAt(0);
        return Character.toUpperCase(a)+name.substring(1);
    }
}
