package com.cout970.magneticraft.api.energy;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface IElectricNode extends INode {

    void iterate();

    double getVoltage();

    double getAmperage();

    double getResistance();

    double getCapacity();

    void applyCurrent(double current);

    void applyPower(double power);
}
