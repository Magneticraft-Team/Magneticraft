package com.cout970.magneticraft.api.electricity;

import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.vectors.Vect3i;

public interface IElectricConductor extends INetworkNode<ElectricNetwork, IElectricConductor> {

    default Voltage getVoltage() {
        return Voltage.LOW;
    }

    @Override
    default boolean isAbleToConnect(IElectricConductor cond, Vect3i offset) {
        return cond.getVoltage() == getVoltage();
    }

    double getResistance();
}
