package com.cout970.magneticraft.api.kinetic;

import com.cout970.magneticraft.api.base.IDataStorage;
import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.InterfaceIdentifierHolder;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface IKineticConductor extends IDataStorage, INetworkNode<KineticNetwork, IKineticConductor> {

    @InterfaceIdentifierHolder(IKineticConductor.class)
    IInterfaceIdentifier<IKineticConductor> IDENTIFIER = null;

    KineticNetwork getNetwork();

    void iterate();

    double getMass();

    double getLoss();

    double getForceConsumed();
}
