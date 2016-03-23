package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.vectors.Vect3i;

/**
 * Created by cout970 on 15/01/2016.
 */
public interface IMultiBlockData {

    Vect3i getControlBlock();

    Direction getDirection();

    MultiBlock getMultiBlock();
}
