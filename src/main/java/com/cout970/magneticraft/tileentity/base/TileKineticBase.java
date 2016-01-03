package com.cout970.magneticraft.tileentity.base;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.network.INetwork;
import net.darkaqua.blacksmith.api.intermod.IInterfaceProvider;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;

/**
 * Created by cout970 on 30/12/2015.
 */
public abstract class TileKineticBase extends TileBase implements IInterfaceProvider {

    protected IKineticConductor cond;

    @Override
    public void bindParent(ITileEntity parent) {
        super.bindParent(parent);
        cond = createKineticConductor();
    }

    public IKineticConductor getCond() {
        return cond;
    }

    @Override
    public void onDelete() {
        super.onDelete();
        INetwork net = cond.getNetwork();
        if (net != null){
            net.removeNetworkNode(cond);
        }
    }

    protected abstract IKineticConductor createKineticConductor();

    @Override
    public Object providerInterface(String className, Class<?> interfaceClass) {
        if (interfaceClass.isAssignableFrom(IKineticConductor.class)){
            return cond;
        }
        return null;
    }

    @Override
    public void update() {
        cond.iterate();
    }

    @Override
    public void loadData(IDataCompound tag) {
        super.loadData(tag);
        cond.load(tag);
    }

    @Override
    public void saveData(IDataCompound tag) {
        super.saveData(tag);
        cond.save(tag);
    }

    public float getRotationAngle(float partialTick) {
        float rot = 0;
        if(getCond().getNetwork() != null){
            double speed = getCond().getNetwork().getSpeed()/20d;
            rot = (float) getCond().getNetwork().getRotationAngle();
            rot += partialTick*speed;
        }
        return rot;
    }
}
