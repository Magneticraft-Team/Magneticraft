package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3i;

/**
 * Created by cout970 on 15/01/2016.
 */
public interface IMultiBlockData {

    Vect3i getControlBlock();

    Direction getDirection();

    MultiBlock getMultiBlock();
}
