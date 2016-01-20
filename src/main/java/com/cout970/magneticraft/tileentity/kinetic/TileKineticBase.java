package com.cout970.magneticraft.tileentity.kinetic;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.KineticNetwork;
import com.cout970.magneticraft.tileentity.TileBase;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.IInterfaceProvider;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.ObjectScanner;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 30/12/2015.
 */
public class TileKineticBase extends TileBase implements IInterfaceProvider, IKineticConductor {
    protected KineticNetwork network;
    protected double mass = 0.01;
    protected double loss = 0.01;

    @Override
    public void onDelete() {
        super.onDelete();
        if (network != null) {
            network.removeNetworkNode(this);
        }
    }

    @Override
    public void update() {
        iterate();
        if (getWorld().getWorldTime() % 20 == 0) {
            for (Direction d : Direction.values()) {
                ITileEntity tile = getWorldReference().move(d).getTileEntity();
                IKineticConductor k = ObjectScanner.findInTileEntity(tile, IKineticConductor.IDENTIFIER, d);

                if (isAbleToConnect(k, d.toVect3i()) && network.canAddToNetwork(k)) {
                    if (k.getNetwork() != null) {
                        network.expandNetwork(k.getNetwork());
                    } else {
                        network.addNetworkNode(k);
                    }
                }
            }
        }
    }

    @Override
    public void loadData(IDataCompound tag) {
        super.loadData(tag);
        mass = tag.getDouble("Mass");
        loss = tag.getDouble("Loss");
    }

    @Override
    public void saveData(IDataCompound tag) {
        super.saveData(tag);
        tag.setDouble("Mass", mass);
        tag.setDouble("Loss", loss);
    }

    public float getRotationAngle(float partialTick) {
        float rot = 0;
        if (network != null) {
            double speed = network.getSpeed() / 20d;
            rot = (float) network.getRotationAngle();
            rot += partialTick * speed;
        }
        return rot;
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
    public double getLoss() {
        return loss;
    }

    @Override
    public double getSpeed() {
        return network.getSpeed();
    }

    @Override
    public WorldRef getWorldReference() {
        if (parent == null) {
            return null;
        }

        return parent.getWorldRef();
    }

    @Override
    public boolean isAbleToConnect(IKineticConductor cond, Vect3i offset) {
        return offset.isDirectionalOffset();
    }

    @Override
    public KineticNetwork getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(KineticNetwork net) {
        network = net;
    }

    @Override
    public boolean isValid() {
        return parent != null && parent.isValid();

    }

    @Override
    public void onNetworkChange() {
    }

    @Override
    public void createNetwork() {
        network = new KineticNetwork(this);
    }

    @Override
    public boolean hasInterface(IInterfaceIdentifier id, Direction direction) {
        return id == IKineticConductor.IDENTIFIER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInterface(IInterfaceIdentifier<T> id, Direction direction) {
        return id == IKineticConductor.IDENTIFIER ? (T) this : null;
    }
}
