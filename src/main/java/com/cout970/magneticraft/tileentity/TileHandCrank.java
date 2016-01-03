package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.defaults.KineticConductor;
import com.cout970.magneticraft.tileentity.base.TileKineticBase;

/**
 * Created by cout970 on 03/01/2016.
 */
public class TileHandCrank extends TileKineticBase {

    @Override
    protected IKineticConductor createKineticConductor() {
        return new KineticConductor(getParent());
    }

    @Override
    public void update() {
        super.update();
//        Log.debug(cond.getNetwork().applyForce(50, 360));
    }
}
