package com.cout970.magneticraft.tileentity.generators;

import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.electricity.ElectricNetwork;
import com.cout970.magneticraft.api.network.Network;
import com.cout970.magneticraft.tileentity.base.TileBase;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.IInterfaceProvider;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cypheraj on 1/14/16.
 */
public class TileCombustionEngine extends TileBase implements IInterfaceProvider, IElectricConductor {
    ElectricNetwork network;

    @Override
    public void update() {

    }

    @Override
    public WorldRef getWorldReference() {
        return getParent().getWorldRef();
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(ElectricNetwork net) {
        network = net;
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
        network = new ElectricNetwork(this);
    }

    @Override
    public boolean hasInterface(IInterfaceIdentifier id, Direction direction) {
        return id == IElectricConductor.IDENTIFIER;
    }

    @Override
    public Object getInterface(IInterfaceIdentifier iInterfaceIdentifier, Direction direction) {
        return this;
    }
}
