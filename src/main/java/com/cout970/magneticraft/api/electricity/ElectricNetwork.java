package com.cout970.magneticraft.api.electricity;

import com.cout970.magneticraft.ManagerApi;
import com.cout970.magneticraft.api.network.Network;
import net.minecraftforge.common.capabilities.Capability;

public class ElectricNetwork extends Network<IElectricConductor> {
    protected Voltage tier;

    public ElectricNetwork(IElectricConductor node) {
        super(node);
        tier = node.getVoltage();
    }

    @Override
    public boolean canAddToNetwork(IElectricConductor node) {
        return (node != null) && (node.getVoltage() == tier);
    }

    public double getTotalResistance() {
        return nodes.stream().mapToDouble(IElectricConductor::getResistance).sum();
    }

    @Override
    public Capability<IElectricConductor> getInterfaceIdentifier() {
        return ManagerApi.ELECTRIC_CONDUCTOR;
    }
}
