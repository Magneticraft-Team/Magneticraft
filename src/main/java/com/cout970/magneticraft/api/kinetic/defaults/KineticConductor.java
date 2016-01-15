package com.cout970.magneticraft.api.kinetic.defaults;

import com.cout970.magneticraft.api.base.IConnectable;
import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.IKineticNetwork;
import com.cout970.magneticraft.api.network.INetwork;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 30/12/2015.
 */
public class KineticConductor implements IKineticConductor {

    protected IKineticNetwork network;
    protected ITileEntity parent;
    protected double mass;
    protected double lose;

    public KineticConductor(ITileEntity parent) {
        this.parent = parent;
        mass = 0.01;
        lose = 0.01;
    }

    @Override
    public void iterate() {
        if (network == null) {
            createNetwork();
            network.refreshNetwork();
        }
        getNetwork().iterate();
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public double getLose() {
        return lose;
    }

    @Override
    public double getSpeed() {
        return getNetwork().getSpeed();
    }

    @Override
    public WorldRef getWorldReference() {
        return parent.getWorldRef();
    }

    @Override
    public boolean isAbleToConnect(IConnectable cond, Vect3i offset) {
        return cond instanceof IKineticConductor;
    }

    @Override
    public void load(IDataCompound data) {
        mass = data.getDouble("Mass");
        lose = data.getDouble("Lose");
    }

    @Override
    public void save(IDataCompound data) {
        data.setDouble("Mass", mass);
        data.setDouble("Lose", lose);
    }

    @Override
    public IKineticNetwork getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(INetwork net) {
        network = (IKineticNetwork) net;
    }

    @Override
    public boolean isValid() {
        return parent.isValid();
    }

    @Override
    public void onNetworkChange() {
    }

    @Override
    public void createNetwork() {
        network = new KineticNetwork(this);
    }
}
