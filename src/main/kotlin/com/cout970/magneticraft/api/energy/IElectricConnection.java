package com.cout970.magneticraft.api.energy;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface IElectricConnection {

    IElectricNode getFirstNode();

    IElectricNode getSecondNode();

    double getSeparationDistance();

    void iterate();
}
