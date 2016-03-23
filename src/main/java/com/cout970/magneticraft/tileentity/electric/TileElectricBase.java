package com.cout970.magneticraft.tileentity.electric;

import com.cout970.magneticraft.ManagerApi;
import com.cout970.magneticraft.api.electricity.ElectricNetwork;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.tileentity.TileBase;
import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Created by cypheraj on 1/17/16.
 */
public class TileElectricBase extends TileBase implements IElectricConductor {

    protected ElectricNetwork network;
    protected double resistance = 0.1;

    @Override
    public double getResistance() {
        return resistance;
    }

    @Override
    public WorldRef getWorldReference() {
        return getWorldRef();
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
        return !isInvalid();
    }

    @Override
    public void onNetworkChange() {
    }

    @Override
    public void createNetwork() {
        network = new ElectricNetwork(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ManagerApi.ELECTRIC_CONDUCTOR || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == ManagerApi.ELECTRIC_CONDUCTOR ? (T) this : super.getCapability(capability, facing);
    }
}
