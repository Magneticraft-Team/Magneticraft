package com.cout970.magneticraft.api.kinetic;

import com.cout970.magneticraft.api.base.IConnectable;
import com.cout970.magneticraft.api.base.IDataStorage;
import com.cout970.magneticraft.api.network.INetworkNode;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface IKineticConductor extends IDataStorage, IConnectable, INetworkNode {

    IKineticNetwork getNetwork();

    void iterate();

    double getMass();

    double getLose();

    double getSpeed();
}
