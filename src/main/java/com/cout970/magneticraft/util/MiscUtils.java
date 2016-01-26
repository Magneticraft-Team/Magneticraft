package com.cout970.magneticraft.util;

import net.darkaqua.blacksmith.api.entity.EntityFactory;
import net.darkaqua.blacksmith.api.entity.types.IEntityItem;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3d;
import net.darkaqua.blacksmith.api.util.WorldRef;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 15/01/2016.
 */
public class MiscUtils {

    public static void rotate(Direction dir) {
        switch (dir) {
            case DOWN:
                GL11.glRotatef(-90, 1, 0, 0);
                break;
            case UP:
                GL11.glRotatef(90, 1, 0, 0);
                break;
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

    public static void rotateAround(float angle, Direction dir, Vect3d axis) {
        switch (dir) {
            case DOWN:
                GL11.glRotatef(angle, (float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
                break;
            case UP:
                GL11.glRotatef(-angle, (float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
                break;
            case NORTH:
                GL11.glRotatef(angle, (float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
                break;
            case SOUTH:
                GL11.glRotatef(-angle, (float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
                break;
            case EAST:
                GL11.glRotatef(angle, (float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
                break;
            case WEST:
                GL11.glRotatef(-angle, (float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
                break;
        }
    }

    public static String capitalize(String name) {
        char a = name.charAt(0);
        return Character.toUpperCase(a) + name.substring(1);
    }

    public static void dropItem(WorldRef ref, Vect3d offset, IItemStack stack) {
        IEntityItem entity = EntityFactory.createEntityItem(ref.getWorld(), ref.getPosition().toVect3d().add(offset), stack);
        ref.getWorld().spawnEntity(entity);
    }
}
