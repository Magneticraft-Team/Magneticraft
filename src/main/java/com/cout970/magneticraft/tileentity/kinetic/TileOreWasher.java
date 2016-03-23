package com.cout970.magneticraft.tileentity.kinetic;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.vectors.Vect3i;

/**
 * Created by cout970 on 05/03/2016.
 */
public class TileOreWasher extends TileKineticBase {

    @Override
    public boolean isAbleToConnect(IKineticConductor cond, Vect3i offset) {
        return Direction.UP.toVect3i().equals(offset);
    }
}
