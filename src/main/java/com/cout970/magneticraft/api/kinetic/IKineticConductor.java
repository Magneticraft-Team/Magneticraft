package com.cout970.magneticraft.api.kinetic;

import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.storage.IDataStorage;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface IKineticConductor extends IDataStorage, INetworkNode<KineticNetwork, IKineticConductor> {

    @Override
    KineticNetwork getNetwork();

    void iterate();

    double getMass();

    double getLoss();

    double getForceConsumed();
}
