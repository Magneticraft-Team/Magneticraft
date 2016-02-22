package com.cout970.magneticraft.api.electricity;

import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.api.common.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.common.intermod.InterfaceIdentifierHolder;
import net.darkaqua.blacksmith.api.common.util.vectors.Vect3i;

public interface IElectricConductor extends INetworkNode<ElectricNetwork, IElectricConductor> {

    @InterfaceIdentifierHolder(IElectricConductor.class)
    IInterfaceIdentifier<IElectricConductor> IDENTIFIER = null;

    default Voltage getVoltage() {
        return Voltage.LOW;
    }

    @Override
    default boolean isAbleToConnect(IElectricConductor cond, Vect3i offset) {
        return cond.getVoltage() == getVoltage();
    }

    double getResistance();
}
