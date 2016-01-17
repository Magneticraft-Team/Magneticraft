package com.cout970.magneticraft.api.electricity;

/**
 * Created by cypheraj on 1/14/16.
 */
public enum Voltage {
    LOW(120),
    MEDIUM(1200),
    HIGH(12000);

    private int maxVoltage;

    Voltage(int maxVoltage) {
        this.maxVoltage = maxVoltage;
    }

    public int getMaxVoltage() {
        return maxVoltage;
    }
}
