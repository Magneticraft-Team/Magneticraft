package com.cout970.magneticraft.util.multiblock;


import net.darkaqua.blacksmith.vectors.Vect3i;

public abstract class MultiBlock {

    protected Vect3i size;//lengths
    protected Vect3i tranlation;
    protected MB_Component[][][] matrix;

    public abstract void init();

    public Vect3i translate(Vect3i rot, IMultiBlockData data) {//yzx
        switch (data.getDirection()){
            case SOUTH:
                return new Vect3i(-rot.getZ(), rot.getX(), -rot.getY()).add(-tranlation.getX(), tranlation.getY(), tranlation.getZ());
            case WEST:
                return new Vect3i(rot.getY(), rot.getX(), -rot.getZ()).add(tranlation.getZ(), tranlation.getY(), -tranlation.getX());
            case EAST:
                return new Vect3i(-rot.getY(), rot.getX(), rot.getZ()).add(tranlation.getZ(), tranlation.getY(), tranlation.getX());
            default:
                return new Vect3i(rot.getZ(), rot.getX(), rot.getY()).add(tranlation.getX(), tranlation.getY(), tranlation.getZ());
        }
    }

    public Vect3i getDimensions(IMultiBlockData data) {
        return size.copy();
    }

    public abstract int getID();

    public MB_Component getCompoenet(int x, int y, int z){
        return matrix[x][y][z];
    }
}
