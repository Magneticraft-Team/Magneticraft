package com.cout970.magneticraft.api.electricity;

import com.cout970.magneticraft.api.network.Network;

public class ElectricNetwork extends Network<IElectricConductor> {

    public ElectricNetwork(IElectricConductor node) {
        super(node);
    }
}
