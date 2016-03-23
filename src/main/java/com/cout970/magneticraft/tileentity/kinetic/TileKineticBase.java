package com.cout970.magneticraft.tileentity.kinetic;

import com.cout970.magneticraft.ManagerApi;
import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.KineticNetwork;
import com.cout970.magneticraft.tileentity.TileBase;
import net.darkaqua.blacksmith.scanner.ObjectScanner;
import net.darkaqua.blacksmith.storage.IDataCompound;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by cout970 on 30/12/2015.
 */
public class TileKineticBase extends TileBase implements IKineticConductor, ICapabilityProvider {

    protected KineticNetwork network;
    protected double mass = 1.5d;

    @Override
    public void invalidate() {
        super.invalidate();
        if (network != null) {
            network.removeNetworkNode(this);
        }
    }

    @Override
    public void update() {
        iterate();
        if (getWorld().getWorldTime() % 20 == 0) {
            for (Direction d : Direction.values()) {
                TileEntity tile = getWorldReference().move(d).getTileEntity();
                IKineticConductor k = ObjectScanner.findInTileEntity(tile, ManagerApi.KINETIC_CONDUCTOR, d.opposite());

                if (network.canAddToNetwork(k) && isAbleToConnect(k, d.toVect3i()) && k.isAbleToConnect(this, d.opposite().toVect3i())) {
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
    }

    @Override
    public void saveData(IDataCompound tag) {
        super.saveData(tag);
        tag.setDouble("Mass", mass);
    }

    public float getRotationAngle(float partialTick) {
        float rot = 0;
        if (network != null) {
            double speed = (network.getSpeed() * 360 / 60) / 20d;
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
        //this is the energy loss caused by the drag
        return getNetwork().getSpeed() * 0.01;
    }

    @Override
    public double getForceConsumed() {
        return 0;
    }

    @Override
    public WorldRef getWorldReference() {
        return getWorldRef();
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
        return !isInvalid();
    }

    @Override
    public void onNetworkChange() {
    }

    @Override
    public void createNetwork() {
        network = new KineticNetwork(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ManagerApi.KINETIC_CONDUCTOR || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == ManagerApi.KINETIC_CONDUCTOR ? (T) this : super.getCapability(capability, facing);
    }
}
