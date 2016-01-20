package com.cout970.magneticraft.api.electricity;

import com.cout970.magneticraft.api.network.IConnectable;
import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.InterfaceIdentifierHolder;
import net.darkaqua.blacksmith.api.util.Vect3i;

public interface IElectricConductor extends IConnectable<IElectricConductor>, INetworkNode<ElectricNetwork> {

    @InterfaceIdentifierHolder(IElectricConductor.class)
    IInterfaceIdentifier<IElectricConductor> IDENTIFIER = null;

    default Voltage getVoltage() {
        return Voltage.LOW;
    }

    @Override
    default boolean isAbleToConnect(IElectricConductor cond, Vect3i offset) {
        return cond.getVoltage() == getVoltage();
    }
}
