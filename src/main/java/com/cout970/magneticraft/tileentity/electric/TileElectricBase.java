package com.cout970.magneticraft.tileentity.electric;

import com.cout970.magneticraft.api.electricity.ElectricNetwork;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.tileentity.TileBase;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.IInterfaceProvider;
import net.darkaqua.blacksmith.api.intermod.InterModUtils;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cypheraj on 1/17/16.
 */
public class TileElectricBase extends TileBase implements IElectricConductor, IInterfaceProvider {
    protected ElectricNetwork network;
    protected double resistance = 0.1;

    @Override
    public double getResistance() {
        return resistance;
    }

    @Override
    public WorldRef getWorldReference() {
        if (parent == null) {
            return null;
        }

        return parent.getWorldRef();
    }

    @Override
    public ElectricNetwork getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(ElectricNetwork net) {
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
        network = new ElectricNetwork(this);
    }

    @Override
    public boolean hasInterface(IInterfaceIdentifier<?> id, Direction direction) {
        return InterModUtils.matches(IElectricConductor.IDENTIFIER, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInterface(IInterfaceIdentifier<T> id, Direction direction) {
        return (T) this;
    }
}
