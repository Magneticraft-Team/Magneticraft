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

    public static float getRotation(Direction dir) {
        switch (dir) {
            case DOWN:
                return 90;
            case UP:
                return -90;
            case NORTH:
                return 180;
            case SOUTH:
                return 0;
            case EAST:
                return 90;
            case WEST:
                return -90;
        }
        return 0;
    }

    public static void rotateAround(float angle, Direction dir, Vect3d axis) {
        switch (dir) {
            case DOWN:
                GL11.glRotatef(angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
                break;
            case UP:
                GL11.glRotatef(-angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
                break;
            case NORTH:
                GL11.glRotatef(angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
                break;
            case SOUTH:
                GL11.glRotatef(-angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
                break;
            case EAST:
                GL11.glRotatef(angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
                break;
            case WEST:
                GL11.glRotatef(-angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
                break;
        }
    }

    public static String capitalize(String name) {
        char a = name.charAt(0);
        return Character.toUpperCase(a) + name.substring(1);
    }

    public static void dropItem(WorldRef ref, Vect3d offset, IItemStack stack, boolean random) {
        if (stack == null) return;
        IEntityItem entity = EntityFactory.createEntityItem(ref.getWorld(), ref.getPosition().toVect3d().add(offset), stack);
        if (!random) {
            entity.setMotion(Vect3d.nullVector());
        }
        ref.getWorld().spawnEntity(entity);
    }

    public static double fastSqrt(double d) {
        //http://stackoverflow.com/questions/13263948/fast-sqrt-in-java-at-the-expense-of-accuracy
        double sqrt = Double.longBitsToDouble(((Double.doubleToLongBits(d) - (1l << 52)) >> 1) + (1l << 61));
        double better = (sqrt + d / sqrt) / 2.0;
        double evenbetter = (better + d / better) / 2.0;
        return evenbetter;
    }
}
