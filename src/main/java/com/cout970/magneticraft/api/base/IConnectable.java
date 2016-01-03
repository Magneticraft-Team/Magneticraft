package com.cout970.magneticraft.api.base;

import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface IConnectable {

    WorldRef getWorldReference();

    boolean isAbleToConnect(IConnectable cond, Vect3i offset);
}
