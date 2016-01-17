package com.cout970.magneticraft.util;

import net.darkaqua.blacksmith.api.util.Direction;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 15/01/2016.
 */
public class MiscUtils {

    public static void rotate(Direction dir) {
        switch (dir) {
            case DOWN:
            case UP:
            case NORTH:
                break;
            case SOUTH:
                GL11.glRotatef(180, 0, 1, 0);
                break;
            case EAST:
                GL11.glRotatef(-90, 0, 1, 0);
                break;
            case WEST:
                GL11.glRotatef(90, 0, 1, 0);
                break;
        }
    }

    public static String capitalize(String name) {
        char a = name.charAt(0);
        return Character.toUpperCase(a)+name.substring(1);
    }
}
