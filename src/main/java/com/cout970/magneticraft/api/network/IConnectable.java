package com.cout970.magneticraft.api.network;

import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3i;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface IConnectable<T extends IConnectable> {

    WorldRef getWorldReference();

    boolean isAbleToConnect(T node, Vect3i offset);
}
