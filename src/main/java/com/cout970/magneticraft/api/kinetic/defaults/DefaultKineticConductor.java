package com.cout970.magneticraft.api.kinetic.defaults;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.KineticNetwork;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 17/01/2016.
 */
public class DefaultKineticConductor implements IKineticConductor {


    @Override
    public KineticNetwork getNetwork() {
        return null;
    }

    @Override
    public <R extends KineticNetwork> void setNetwork(R net) {
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void onNetworkChange() {
    }

    @Override
    public void createNetwork() {

    }

    @Override
    public void iterate() {

    }

    @Override
    public double getMass() {
        return 1;
    }

    @Override
    public double getLoss() {
        return 0;
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public WorldRef getWorldReference() {
        return null;
    }

    @Override
    public boolean isAbleToConnect(IKineticConductor node, Vect3i offset) {
        return false;
    }

    @Override
    public void loadData(IDataCompound data) {}

    @Override
    public void saveData(IDataCompound data) {}
}
