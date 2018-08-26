package com.cout970.magneticraft.api.energy;

import com.cout970.magneticraft.api.core.INode;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface IElectricNode extends INode {

    /**
     * Returns the voltage of the node
     */
    double getVoltage();

    /**
     * Returns an approximate value of the total flow of charge in this node in the last tick this is only used to
     * calculate the energy flowing through a wire
     *
     * @return The amperage of the last tick
     */
    double getAmperage();

    /**
     * Returns the electrical resistance of the node
     */
    double getResistance();

    /**
     * Returns the capacity of the node This is not energy storage, this is used to calculate how long will take to fill
     * the node with charge The default value is 1.0
     */
    double getCapacity();

    /**
     * Adds or removes an amount of charge to the node Current can be negative All the charge will be added or removed
     * completely
     *
     * @param current The amount of charge to add/remove to this node
     */
    void applyCurrent(double current);

    /**
     * Adds or removes energy from the node, returns the power added/removed Power can be negative The return value is
     * always positive
     *
     * @param power The energy to add or remove from the node in Watts
     * @param simulated true if this is just a test that shouldn't modify the node, or false if the power should be
     * applied
     *
     * @return The energy inserted or extracted from the node
     */
    double applyPower(double power, boolean simulated);
}
